package com.nhnacademy.illuwa.domain.order.exception.shippingPolicy;

import lombok.Getter;

@Getter
public class ShippingPolicyNotFoundException extends RuntimeException {
    private final long id;
    public ShippingPolicyNotFoundException(long id) {
        super("해당 정책을 찾을 수 없습니다." + id);
        this.id = id;
    }
}
