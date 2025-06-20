package com.nhnacademy.illuwa.domain.order.dto.orderItem;

import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class OrderItemResponseDto {

    private long orderItemId;
    private long bookId;
    private int quantity;
    private BigDecimal price;
    private long packagingId;

    @QueryProjection
    public OrderItemResponseDto(long orderItemId, long bookId, int quantity, BigDecimal price, long packagingId) {
        this.orderItemId = orderItemId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
        this.packagingId = packagingId;
    }

}

// order 서버 -> 프론트 (개별 주문 상품 조회 요청)