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


@Component("cartPayloadProvider")
@RequiredArgsConstructor
public class CartPayloadPriceProvider implements ItemPriceProvider {
    private final MemberCouponService memberCouponService;
    private final DiscountCalculator discountCalculator;
    private final ProductApiClient cartApiClient;

    @Override
    public ItemPrice fetchPrice(Long bookId, int quantity, Long couponId, Optional<CartPayload> payload) {
        CartPayload p = payload.orElseGet(() -> {
            // 장바구니 Payload가 없으면 Cart API 호출로 보충
            BookPriceDto dto = cartApiClient.getBookPriceByBookId(bookId).orElseThrow(()
            -> new NotFoundException("해당 도서의 가격을 찾을 수 없습니다."));

            BigDecimal unitPrice = dto.getSalePrice() != null
                    ? dto.getSalePrice()
                    : dto.getRegularPrice();


            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            return new CartPayload(unitPrice, quantity, totalPrice);
        });
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
