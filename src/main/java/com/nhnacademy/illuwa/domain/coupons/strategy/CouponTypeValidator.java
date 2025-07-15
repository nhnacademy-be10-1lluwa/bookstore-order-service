package com.nhnacademy.illuwa.domain.coupons.strategy;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;

public interface CouponTypeValidator {

    CouponType getType();

    void validate(CouponCreateRequest request, ProductApiClient productApi);
}
