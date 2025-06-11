package com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicyCreateRequest {

    @NotBlank
    private String code;

    @NotNull
    private int minOrderAmount; // 최소 주문 금액

    private Integer discountAmount; // 할인 금액

    private Integer discountPercent; // 할인 퍼센트

    private Integer maxDiscountAmount; // 최대 할인 금액

}
