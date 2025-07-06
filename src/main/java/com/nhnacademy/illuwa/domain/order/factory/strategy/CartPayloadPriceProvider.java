package com.nhnacademy.illuwa.domain.order.factory.strategy;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component("cartPayloadProvider")
public class CartPayloadPriceProvider implements ItemPriceProvider {
    private final MemberCouponService memberCouponService;
    private final DiscountCalculator discountCalculator;

    public CartPayloadPriceProvider(MemberCouponService memberCouponService, DiscountCalculator discountCalculator) {
        this.memberCouponService = memberCouponService;
        this.discountCalculator = discountCalculator;
    }

    @Override
    public ItemPrice fetchPrice(Long bookId, int quantity, Long couponId, Optional<CartPayload> payload) {
        CartPayload p = payload.orElseThrow(() -> new NotFoundException("해당 장바구니를 찾을 수 없습니다."));
        BigDecimal gross = p.totalPrice();
        BigDecimal discount = BigDecimal.ZERO;

        if (couponId != null) {
            MemberCouponDiscountDto dto = memberCouponService.getDiscountPrice(couponId);
            BigDecimal after = discountCalculator.calculate(
                    gross, dto.getDiscountAmount(), dto.getDiscountPercent());
            discount = gross.subtract(after);
        }

        return new ItemPrice(
                p.unitPrice(),
                gross,
                discount
        );
    }
}
