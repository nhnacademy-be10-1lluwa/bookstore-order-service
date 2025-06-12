package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
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
public class CouponPolicyResponse {
    private Long id;

    private String code;

    private BigDecimal minOrderAmount; // 최소 주문 금액

    private BigDecimal discountAmount; // 할인 금액

    private BigDecimal discountPercent; // 할인 퍼센트

    private BigDecimal maxDiscountAmount; // 최대 할인 금액

    private CouponStatus status; // 상태 여부

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // 정적 팩토리 메서드 사용
    public static CouponPolicyResponse fromEntity(CouponPolicy policy) {
        if (policy == null) {
            return null;
        }
        return CouponPolicyResponse.builder()
                .id(policy.getId())
                .code(policy.getCode())
                .status(policy.getStatus())
                .minOrderAmount(policy.getMinOrderAmount())
                .discountAmount(policy.getDiscountAmount())
                .discountPercent(policy.getDiscountPercent())
                .maxDiscountAmount(policy.getMaxDiscountAmount())
                .createAt(policy.getCreateAt())
                .updateAt(policy.getUpdateAt())
                .build();
    }

}
