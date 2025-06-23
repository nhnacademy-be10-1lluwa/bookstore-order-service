package com.nhnacademy.illuwa.domain.coupons.exception.memberCoupon;

public class MemberCouponIsUsed extends RuntimeException {
    public MemberCouponIsUsed(String message) {
        super(message);
    }
}
