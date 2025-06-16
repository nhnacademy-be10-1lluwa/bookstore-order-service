package com.nhnacademy.illuwa.domain.order.dto.orderItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateRequestDto {
    private long bookId;
    private long orderId;
    private int quantity;
    private BigDecimal price;
    private long packagingId;
    private BigDecimal discountPrice;
}

// 프론트 -> order 서버 (개별 주문 요청): 주문은 OrderCreateRequestDto 를 사용해서 한번에 되나 service 레이어에서 orderItem 테이블에 넣기 위함
