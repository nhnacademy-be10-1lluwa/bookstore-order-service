package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

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
public class CouponPolicyResponse {
    private Long id;

    private String code;

    private int minOrderAmount; // 최소 주문 금액

    private Integer discountAmount; // 할인 금액

    private Integer discountPercent; // 할인 퍼센트

    private Integer maxDiscountAmount; // 최대 할인 금액

    private CouponStatus status; // 상태 여부

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
