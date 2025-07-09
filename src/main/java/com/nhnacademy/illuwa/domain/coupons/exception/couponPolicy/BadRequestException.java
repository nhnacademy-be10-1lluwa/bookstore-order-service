package com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
