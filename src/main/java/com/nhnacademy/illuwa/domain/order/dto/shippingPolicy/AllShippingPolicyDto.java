package com.nhnacademy.illuwa.domain.order.dto.shippingPolicy;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AllShippingPolicyDto {
    private long shippingPolicyId;
    private BigDecimal minAmount;
    private BigDecimal fee;
    private boolean active;

    @QueryProjection
    public AllShippingPolicyDto(long shippingPolicyId, BigDecimal minAmount, BigDecimal fee, boolean active) {
        this.shippingPolicyId = shippingPolicyId;
        this.minAmount = minAmount;
        this.fee = fee;
        this.active = active;
    }
}

// order 서버 -> 프론트 (활성화 된 정책만 전송) 필요시 사용