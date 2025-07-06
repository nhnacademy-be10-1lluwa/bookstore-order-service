package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundStringException;
import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.common.external.product.dto.CreateOrderFromCartRequest;
import com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto;
import com.nhnacademy.illuwa.domain.order.factory.GuestOrderCartFactory;
import com.nhnacademy.illuwa.domain.order.factory.GuestOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderCartFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final GuestOrderCartFactory guestOrderCartFactory;
    private final MemberOrderCartFactory memberOrderCartFactory;
    private final ProductApiClient productApiClient;
    private final MemberCouponService memberCouponService;
    private final UserApiClient userApiClient;
    private final PackagingService packagingService;
    private final GuestOrderDirectFactory guestOrderDirectFactory;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getAllOrders(Pageable pageable) {
        return orderRepository.findOrderDtos(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        return orderRepository.findOrderDto(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));
    }

    @Override
    public OrderResponseDto getOrderByNumber(String orderNumber) {
        return orderRepository.findOrderDtoByOrderNumber(orderNumber).orElseThrow(() ->
                new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable).map(OrderListResponseDto::orderListResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByOrderStatus(status, pageable).map(OrderListResponseDto::orderListResponseDto);
    }

    // member 주문하기 (cart)
    @Override
    public Order memberCreateOrderFromCartWithItems(Long memberId, MemberOrderRequest request) {
        return memberOrderCartFactory.createMemberOrderCart(memberId, request);
    }

    // guest 주문하기 (cart)
    @Override
    public Order guestCreateOrderFromCartWithItems(GuestOrderRequest request) {
        return guestOrderCartFactory.createGuestOrderCart(request);
    }

    // guest 주문하기 (direct)
    @Override
    public Order guestCreateOrderDirectWithItems(GuestOrderRequestDirect request) {
        return guestOrderDirectFactory.createGuestOrderDirect(request);
    }

    @Override
    public Order memberCreateOrderDirectWithItems(MemberOrderRequestDirect request) {
        return null;
    }


    @Override
    public void cancelOrderById(Long orderId) {
        orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));

        orderRepository.updateOrderStatusByOrderId(orderId, OrderStatus.Cancelled);

    }

    @Override
    public void cancelOrderByOrderNumber(String orderNumber) {
        orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto) {
        orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));

        orderRepository.updateOrderStatusByOrderId(orderId, orderUpdateDto.getOrderStatus());
    }

    @Override
    public void updateOrderStatusByOrderNumber(String orderNumber, OrderUpdateStatusDto orderUpdateDto) {
        orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    public MemberOrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId) {
        CreateOrderFromCartRequest request = productApiClient.getCart(memberId).orElseThrow(()
                -> new NotFoundException("장바구니를 찾을 수 없습니다.", memberId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<MemberAddressDto> addresses = userApiClient.getAddressByMemberId(memberId);
        List<MemberCouponResponse> coupons = memberCouponService.getAllMemberCoupons(memberId);
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId).orElseThrow(()
                -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId)).getPoint();

        return new MemberOrderInitFromCartResponseDto(cartItems, addresses, coupons, packaging, pointBalance);
    }

    @Override
    public GuestOrderInitFromCartResponseDto getGuestOrderInitFromCartData(Long cartId) {
        CreateOrderFromCartRequest request = productApiClient.getGuestCart(cartId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", cartId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);

        return new GuestOrderInitFromCartResponseDto(cartItems, packaging);
    }

}
