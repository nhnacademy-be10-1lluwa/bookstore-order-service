package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;

import java.util.List;
import java.util.Optional;


// todo #1-3 Querydsl 인터페이스 생성
public interface OrderQuerydslRepository {
    List<OrderListResponseDto> findOrderDtos();

    Optional<OrderResponseDto> findOrderDto(Long orderId);
}
