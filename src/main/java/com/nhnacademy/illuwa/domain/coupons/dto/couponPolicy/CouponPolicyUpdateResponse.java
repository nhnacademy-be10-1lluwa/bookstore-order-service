package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicyUpdateResponse {

   private String code;
   private CouponStatus status;
   private Integer minOrderAmount;
   private Integer discountAmount;
   private Integer discountPercent;
   private Integer maxDiscountAmount;
   private LocalDateTime updatedAt;

    public static CouponPolicyUpdateResponse fromEntity(CouponPolicy policy) {
        return CouponPolicyUpdateResponse.builder()
                .code(policy.getCode())
                .status(policy.getStatus())
                .minOrderAmount(policy.getMinOrderAmount())
                .discountAmount(policy.getDiscountAmount())
                .discountPercent(policy.getDiscountPercent())
                .maxDiscountAmount(policy.getMaxDiscountAmount())
                .updatedAt(policy.getUpdateAt())
                .build();
    }
}
