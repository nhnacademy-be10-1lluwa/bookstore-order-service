package com.nhnacademy.illuwa.domain.order.dto.shippingPolicy;

import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class ShippingPolicyResponseDto {
    private long shippingPolicyId;
    private BigDecimal minAmount;
    private BigDecimal fee;

    @QueryProjection
    public ShippingPolicyResponseDto(long shippingPolicyId, BigDecimal minAmount, BigDecimal fee) {
        this.shippingPolicyId = shippingPolicyId;
        this.minAmount = minAmount;
        this.fee = fee;
    }

    public static ShippingPolicyResponseDto fromEntity(ShippingPolicy shippingPolicy) {
        return ShippingPolicyResponseDto.builder()
                .shippingPolicyId(shippingPolicy.getShippingPolicyId())
                .minAmount(shippingPolicy.getMinAmount())
                .fee(shippingPolicy.getFee())
                .build();
    }
}

// order 서버 -> 프론트 (배송 정책 정보 전송)