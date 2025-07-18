package com.nhnacademy.illuwa.domain.coupons.exception.coupon;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String message) {
        super(message);
    }
}
