package com.nhnacademy.illuwa.domain.order.dto.shippingPolicy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveShippingPolicyDto {
    private BigDecimal minAmount;
    private BigDecimal fee;
}

// order 서버 -> 프론트 (활성화 된 정책만 전송) 필요시 사용