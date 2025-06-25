package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicyCreateResponse {

   private Long id;
   private String code;
   private CouponStatus status;
   private DiscountType discountType;
   private BigDecimal minOrderAmount;
   private BigDecimal discountAmount;
   private BigDecimal discountPercent;
   private BigDecimal maxDiscountAmount;
   private LocalDateTime createdAt;

    public static CouponPolicyCreateResponse fromEntity(CouponPolicy policy) {
        return CouponPolicyCreateResponse.builder()
                .id(policy.getId())
                .code(policy.getCode())
                .status(policy.getStatus())
                .discountType(policy.getDiscountType())
                .minOrderAmount(policy.getMinOrderAmount())
                .discountAmount(policy.getDiscountAmount())
                .discountPercent(policy.getDiscountPercent())
                .maxDiscountAmount(policy.getMaxDiscountAmount())
                .createdAt(policy.getCreateAt())
                .build();
    }
}
