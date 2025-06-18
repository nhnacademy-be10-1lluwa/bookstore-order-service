package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;

import java.util.List;


// todo #1-3 Querydsl 인터페이스 생성
public interface OrderQuerydslRepository {
    List<OrderListResponseDto> findOrderItemDtos();

}
