package com.nhnacademy.illuwa.domain.coupons.strategy;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;

public interface DiscountPolicyStrategy {
    DiscountType getType();
    void discountValidate(CouponPolicyCreateRequest request);
}
