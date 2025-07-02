package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundStringException;
import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.common.external.product.dto.CreateOrderFromCartRequest;
import com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto;
import com.nhnacademy.illuwa.domain.order.factory.OrderFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderFactory orderFactory;
    private final ProductApiClient productApiClient;
    private final MemberCouponService memberCouponService;
    private final UserApiClient userApiClient;
    private final PackagingService packagingService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderFactory orderFactory, ProductApiClient productApiClient, MemberCouponService memberCouponService, UserApiClient userApiClient, PackagingService packagingService) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.productApiClient = productApiClient;
        this.memberCouponService = memberCouponService;
        this.userApiClient = userApiClient;
        this.packagingService = packagingService;
    }

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

    @Override
    public Order createOrderWithItems(OrderCreateRequestDto dto) {
        return orderFactory.createOrder(dto);
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
    public OrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId) {
        CreateOrderFromCartRequest request = productApiClient.getCart(memberId).orElseThrow(()
                -> new NotFoundException("장바구니를 찾을 수 없습니다.", memberId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<MemberAddressDto> addresses = userApiClient.getAddressByMemberId(memberId);
        List<MemberCouponResponse> coupons = memberCouponService.getAllMemberCoupons(memberId);
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId).orElseThrow(()
                -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId)).getPoint();

        return new OrderInitFromCartResponseDto(cartItems, addresses, coupons, packaging, pointBalance);
    }

    @Override
    public GuestOrderInitResponseDto getGuestOrderInitFromCartData(Long cartId) {
        CreateOrderFromCartRequest request = productApiClient.getGuestCart(cartId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", cartId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);

        return new GuestOrderInitResponseDto(cartItems, packaging);
    }

}
