package com.nhnacademy.illuwa.domain.order.dto.shippingPolicy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingPolicyResponseDto {
    private long shippingPolicyId;
    private BigDecimal minAmount;
    private BigDecimal fee;
    private boolean isActive;
}

// order 서버 -> 프론트 (배송 정책 정보 전송)