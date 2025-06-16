package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;

import java.util.List;

public interface OrderItemService {

    // 전체 주문 아이템 내역 조회(ADMIN)
    List<OrderItemResponseDto> getAllOrderItem();

    // id로 주문 아이템 내역 조회(ADMIN, MEMBERS)
    OrderItemResponseDto getOrderItemById(String orderItemId);

    // member 별 주문 아이템 내역 조회(ADMIN, MEMBERS)
    List<OrderItemResponseDto> getOrderItemByMemberId(String memberId);

    // 주문별 주문 아이템 내역 조회 (MEMBERS, ADMIN)
    List<OrderItemResponseDto> getOrderItemByOrderId(String orderId);

    // 주문하기 (아이템 개별)
    OrderItem addOrderItem(OrderItemCreateRequestDto dto);

}
