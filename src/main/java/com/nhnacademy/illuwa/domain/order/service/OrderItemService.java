package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;

import java.util.List;

public interface OrderItemService {

    // 전체 주문 아이템 내역 조회(ADMIN)
    List<OrderItemResponseDto> getAllOrderItem();

    // id로 주문 아이템 내역 조회(ADMIN, MEMBERS)
    OrderItemResponseDto getOrderItemById(Long orderItemId);

    // 주문별 주문 아이템 내역 조회 (MEMBERS, ADMIN)
    List<OrderItemResponseDto> getOrderItemByOrderId(Long orderId);

}
