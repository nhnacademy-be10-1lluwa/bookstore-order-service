package com.nhnacademy.illuwa.domain.order.dto.order;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.nhnacademy.illuwa.domain.order.dto.order.QOrderResponseDto is a Querydsl Projection type for OrderResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderResponseDto extends ConstructorExpression<OrderResponseDto> {

    private static final long serialVersionUID = -1129916486L;

    public QOrderResponseDto(com.querydsl.core.types.Expression<Long> orderId, com.querydsl.core.types.Expression<Long> memberId, com.querydsl.core.types.Expression<java.time.LocalDateTime> orderDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> deliveryDate, com.querydsl.core.types.Expression<? extends java.math.BigDecimal> totalPrice, com.querydsl.core.types.Expression<com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus> orderStatus) {
        super(OrderResponseDto.class, new Class<?>[]{long.class, long.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, java.math.BigDecimal.class, com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus.class}, orderId, memberId, orderDate, deliveryDate, totalPrice, orderStatus);
    }

}

