package com.nhnacademy.illuwa.domain.coupons.strategy.registry;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.strategy.CouponTypeValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CouponTypeValidatorRegistry {
    private final Map<CouponType, CouponTypeValidator> registry;

    public CouponTypeValidatorRegistry(List<CouponTypeValidator> validators) {
        this.registry = validators.stream()
                .collect(Collectors.toMap(CouponTypeValidator::getType,
                        v -> v));
    }

    public CouponTypeValidator getValidator(CouponType type) {
        if (!registry.containsKey(type)) {
            throw new IllegalArgumentException("지원하지 않는 쿠폰 타입입니다: " + type);
        }

        return registry.get(type);
    }
}
