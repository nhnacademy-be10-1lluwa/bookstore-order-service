package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    // 전체 주문 내역 조회(ADMIN)
    List<OrderListResponseDto> getAllOrders();

    // id로 주문 내역 조회(ADMIN, MEMBERS)
    OrderResponseDto getOrderById(String orderId);

    // member 별 주문 내역 조회 (ADMIN, MEMBERS)
    List<OrderListResponseDto> getOrderByMemberId(String memberId);

    // 주문 상태별 조회 (ADMIN)
    List<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status);

    // 주문하기(MEMBERS)
    Order createOrderWithItems(String memberId, OrderCreateRequestDto dto);

    // 주문 취소하기(MEMBERS)
    int cancelOrderById(String orderId);

    // 주문 상태 변경하기(ADMIN)
    int updateOrderStatus(String orderId, OrderUpdateStatusDto orderUpdateDto);

}
