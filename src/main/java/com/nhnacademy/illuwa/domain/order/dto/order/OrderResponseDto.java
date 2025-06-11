package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private long orderId;
    private long memberId;
    private ZonedDateTime orderDate;
    private ZonedDateTime deliveryDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
}

// order 서버 -> 프론트 (주문 상세 조회 응답)
