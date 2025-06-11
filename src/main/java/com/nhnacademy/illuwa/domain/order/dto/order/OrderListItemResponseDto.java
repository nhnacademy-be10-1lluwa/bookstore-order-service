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
public class OrderListItemResponseDto {

    private long orderId;
    private ZonedDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
}

// order 서버 -> 프론트 (Admin/ 주문 목록 응답(리스트용) )
