package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // 전체 주문 내역 조회(ADMIN)
    Page<OrderListResponseDto> getAllOrders(Pageable pageable);

    // id로 주문 내역 조회(ADMIN, MEMBERS)
    OrderResponseDto getOrderById(Long orderId);

    // number 로 주문 내역 조회(ADMIN, MEMBERS)
    OrderResponseDto getOrderByNumber(String orderNumber);

    // member 별 주문 내역 조회 (ADMIN, MEMBERS)
    Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable);

    // 주문 상태별 조회 (ADMIN)
    Page<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status, Pageable pageable);

    // 주문하기(MEMBERS)
    Order createOrderWithItems(OrderCreateRequestDto dto);

    // id로 주문 취소하기(MEMBERS)
    void cancelOrderById(Long orderId);

    // number로 주문 취소하기(MEMBERS)
    void cancelOrderByOrderNumber(String orderNumber);

    // id로 주문 상태 변경하기(ADMIN)
    void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto);

    // number로 주문 상태 변경하기 (ADMIN)
    void updateOrderStatusByOrderNumber(String orderNumber, OrderUpdateStatusDto orderUpdateDto);

    // 주문 초기 데이터 조회(member)
    OrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId);

    // 주문 초기 데이터 조회(Guest)
    GuestOrderInitResponseDto getGuestOrderInitFromCartData(Long cartId);
    }
