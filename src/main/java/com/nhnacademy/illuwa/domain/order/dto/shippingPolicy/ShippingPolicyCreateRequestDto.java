package com.nhnacademy.illuwa.domain.order.dto.shippingPolicy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingPolicyCreateRequestDto {
    private BigDecimal minAmount;
    private BigDecimal fee;
}

// 프로트 -> 주문 서버 (배송 정책 등록)
