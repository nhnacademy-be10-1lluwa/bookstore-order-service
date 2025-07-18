package com.nhnacademy.illuwa.domain.coupons.strategy.impl.couponpolicy;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.BadRequestException;
import com.nhnacademy.illuwa.domain.coupons.strategy.DiscountPolicyStrategy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AmountPolicyValidator implements DiscountPolicyStrategy {

    @Override
    public DiscountType getType() {
        return DiscountType.AMOUNT;
    }

    @Override
    public void discountValidate(CouponPolicyCreateRequest request) {
        if (Objects.isNull(request.getDiscountAmount())) {
            throw new BadRequestException("금액 할인 정책은 discountAmount 값이 필요합니다.");
        }
        if (Objects.nonNull(request.getDiscountPercent())) {
            throw new BadRequestException("금액 할인 정책은 discountPercent 값이 존재하면 안됩니다.");
        }
    }
}
