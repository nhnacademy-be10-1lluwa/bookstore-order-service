package com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy;

public class CouponPolicyExistsByCodeException extends RuntimeException {
    public CouponPolicyExistsByCodeException(String message) {
        super(message);

    }
}
