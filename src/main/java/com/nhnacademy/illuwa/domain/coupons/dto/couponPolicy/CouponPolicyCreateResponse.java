package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicyCreateResponse {

   private Long id;
   private String code;
   private CouponStatus status;
   private Integer minOrderAmount;
   private Integer discountAmount;
   private Integer discountPercent;
   private Integer maxDiscountAmount;
   private LocalDateTime createdAt;

    public static CouponPolicyCreateResponse fromEntity(CouponPolicy policy) {
        return CouponPolicyCreateResponse.builder()
                .id(policy.getId())
                .code(policy.getCode())
                .status(policy.getStatus())
                .minOrderAmount(policy.getMinOrderAmount())
                .discountAmount(policy.getDiscountAmount())
                .discountPercent(policy.getDiscountPercent())
                .maxDiscountAmount(policy.getMaxDiscountAmount())
                .createdAt(policy.getCreateAt())
                .build();
    }
}
