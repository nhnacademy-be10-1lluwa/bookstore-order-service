package com.nhnacademy.illuwa.domain.order.dto.orderItem;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.nhnacademy.illuwa.domain.order.dto.orderItem.QOrderItemResponseDto is a Querydsl Projection type for OrderItemResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderItemResponseDto extends ConstructorExpression<OrderItemResponseDto> {

    private static final long serialVersionUID = -1735819948L;

    public QOrderItemResponseDto(com.querydsl.core.types.Expression<Long> orderItemId, com.querydsl.core.types.Expression<Long> bookId, com.querydsl.core.types.Expression<Integer> quantity, com.querydsl.core.types.Expression<? extends java.math.BigDecimal> price, com.querydsl.core.types.Expression<Long> packagingId) {
        super(OrderItemResponseDto.class, new Class<?>[]{long.class, long.class, int.class, java.math.BigDecimal.class, long.class}, orderItemId, bookId, quantity, price, packagingId);
    }

}

