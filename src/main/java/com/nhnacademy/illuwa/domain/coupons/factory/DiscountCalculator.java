package com.nhnacademy.illuwa.domain.coupons.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Component
public class DiscountCalculator {

    /// @return 상품의 할인 적용후 금액
    /// 주문 로직에서 사용
    public BigDecimal calculate(BigDecimal orderAmount, BigDecimal discountAmount, BigDecimal discountPercent) {
        if (discountAmount != null) {
            return orderAmount.subtract(discountAmount);
        } else if (discountPercent != null) {
            BigDecimal rate = discountPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            return orderAmount.subtract(orderAmount.multiply(rate));
        }
        return orderAmount;
    }
}
