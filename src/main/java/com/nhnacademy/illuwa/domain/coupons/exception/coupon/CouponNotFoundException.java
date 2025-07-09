package com.nhnacademy.illuwa.domain.coupons.exception.coupon;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String message) {
        super(message);
    }
}
