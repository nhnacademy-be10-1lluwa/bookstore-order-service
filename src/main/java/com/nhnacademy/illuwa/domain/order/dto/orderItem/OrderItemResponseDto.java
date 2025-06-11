package com.nhnacademy.illuwa.domain.order.dto.orderItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {

    private long orderItemId;
    private long bookId;
    private int quantity;
    private BigDecimal price;
    private long packagingId;
}

// order 서버 -> 프론트 (개별 주문 상품 조회 요청)