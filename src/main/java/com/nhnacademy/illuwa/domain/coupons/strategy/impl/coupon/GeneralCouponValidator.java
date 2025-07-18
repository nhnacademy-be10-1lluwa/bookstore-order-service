package com.nhnacademy.illuwa.domain.coupons.strategy.impl.coupon;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.strategy.CouponTypeValidator;
import org.springframework.stereotype.Component;

@Component
public class GeneralCouponValidator implements CouponTypeValidator {
    @Override
    public CouponType getType() {
        return CouponType.GENERAL;
    }

    @Override
    public void validate(CouponCreateRequest request, ProductApiClient productApi) {

    }
}
