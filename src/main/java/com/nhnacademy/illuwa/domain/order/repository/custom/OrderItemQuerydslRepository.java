package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemQuerydslRepository {

    List<OrderItemResponseDto> findAllOrderItemDtos();

    Optional<OrderItemResponseDto> findOrderItemDtoByOrderItemId(Long orderItemId);

    List<OrderItemResponseDto> findOrderItemDtosByOrderId(Long orderId);

    List<OrderItemResponseDto> findOrderItemDtosByOrderNumber(String orderNumber);

}
