package com.nhnacademy.illuwa.domain.order.factory.strategy;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookPriceDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component("bookApiProvider")
@RequiredArgsConstructor
public class BookApiPriceProvider implements ItemPriceProvider {
    private final ProductApiClient productApiClient;
    private final DiscountCalculator discountCalculator;
    private final MemberCouponService memberCouponService;

    @Override
    public ItemPrice fetchPrice(Long bookId, int quantity, Long couponId, Optional<CartPayload> payload) {

        BookPriceDto dto = productApiClient.getBookPriceByBookId(bookId)
                .orElseThrow(() -> new NotFoundException("도서 가격 정보를 찾을 수 없습니다.", bookId));
        BigDecimal unit = dto.getPriceSales() != null
                ? dto.getPriceSales()
                : dto.getPriceStandard();

        BigDecimal discount = BigDecimal.ZERO;
        if (couponId != null) {
            MemberCouponDiscountDto dtoDiscount = memberCouponService.getDiscountPrice(couponId);
            BigDecimal gross = unit.multiply(BigDecimal.valueOf(quantity));
            BigDecimal after = discountCalculator.calculate(gross,
                    dtoDiscount.getDiscountAmount(), dtoDiscount.getDiscountPercent());
            discount = gross.subtract(after);
        }

        BigDecimal gross = unit.multiply(BigDecimal.valueOf(quantity));
        return new ItemPrice(unit, gross, discount);
    }
}
