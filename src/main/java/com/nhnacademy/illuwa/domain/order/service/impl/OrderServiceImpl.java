package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundStringException;
import com.nhnacademy.illuwa.domain.order.external.cart.CartApiClient;
import com.nhnacademy.illuwa.domain.order.external.cart.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.order.external.cart.dto.CreateOrderFromCartRequest;
import com.nhnacademy.illuwa.domain.order.external.member.MemberAddressApiClient;
import com.nhnacademy.illuwa.domain.order.external.member.MemberPointApiClient;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberAddressDto;
import com.nhnacademy.illuwa.domain.order.factory.OrderFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
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
    private final CartApiClient cartApiClient;
    private final MemberAddressApiClient memberAddressApiClient;
    private final CouponService couponService;
    private final MemberCouponService memberCouponService;
    private final MemberPointApiClient memberPointApiClient;

    public OrderServiceImpl(OrderRepository orderRepository, OrderFactory orderFactory, CartApiClient cartApiClient, MemberAddressApiClient memberAddressApiClient, CouponService couponService, MemberCouponService memberCouponService, MemberPointApiClient memberPointApiClient) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
        this.cartApiClient = cartApiClient;
        this.memberAddressApiClient = memberAddressApiClient;
        this.couponService = couponService;
        this.memberCouponService = memberCouponService;
        this.memberPointApiClient = memberPointApiClient;
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
    public Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable ) {
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

    public OrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId) {
        CreateOrderFromCartRequest request = cartApiClient.getCart(memberId).orElseThrow(()
                -> new NotFoundException("장바구니를 찾을 수 없습니다.", memberId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<MemberAddressDto> addresses = memberAddressApiClient.getAddressByMemberId(memberId);
        List<MemberCouponResponse> coupons = memberCouponService.getAllMemberCoupons(memberId);
        BigDecimal pointBalance = memberPointApiClient.getPointByMemberId(memberId).orElseThrow(()
        -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId)).getPoint();

        return new OrderInitFromCartResponseDto(cartItems, addresses, coupons, pointBalance);
    }

}
