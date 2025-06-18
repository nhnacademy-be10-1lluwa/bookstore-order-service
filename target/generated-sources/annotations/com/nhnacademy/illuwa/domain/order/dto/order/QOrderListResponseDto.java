package com.nhnacademy.illuwa.domain.order.dto.order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.nhnacademy.illuwa.domain.order.dto.order.QOrderListResponseDto is a Querydsl Projection type for OrderListResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderListResponseDto extends ConstructorExpression<OrderListResponseDto> {

    private static final long serialVersionUID = 1069519420L;

    public QOrderListResponseDto(com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderDate, com.querydsl.core.types.Expression<? extends java.math.BigDecimal> totalPrice, com.querydsl.core.types.Expression<com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus> orderStatus) {
        super(OrderListResponseDto.class, new Class<?>[]{long.class, java.time.LocalDateTime.class, java.math.BigDecimal.class, com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus.class}, orderId, orderDate, totalPrice, orderStatus);
    }

}

