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
public class OrderCreateRequestDto {

    private long shippingPolicyId;
    private ZonedDateTime orderDate;
    private ZonedDateTime deliveryDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private String password;
}

// 프론트 -> order 서버 (주문 요청)