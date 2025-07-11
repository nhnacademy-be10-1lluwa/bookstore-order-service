package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicyUpdateRequest{
    private BigDecimal minOrderAmount; // 최소 주문 금액

    private BigDecimal discountAmount; // 할인 금액

    private BigDecimal discountPercent; // 할인 퍼센트

    private BigDecimal maxDiscountAmount; // 최대 할인 금액

    private CouponStatus status; // 상태 여부 수정


    public void updateTo(CouponPolicy policy) {
        if(minOrderAmount != null) policy.setMinOrderAmount(minOrderAmount);
        if (minOrderAmount != null) policy.setMinOrderAmount(minOrderAmount);
        if (discountAmount != null) policy.setDiscountAmount(discountAmount);
        if (discountPercent != null) policy.setDiscountPercent(discountPercent);
        if (maxDiscountAmount != null) policy.setMaxDiscountAmount(maxDiscountAmount);
        if (status != null) policy.setStatus(status);
    }
}
