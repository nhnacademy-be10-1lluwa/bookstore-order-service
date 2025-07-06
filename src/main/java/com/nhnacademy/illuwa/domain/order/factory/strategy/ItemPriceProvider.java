package com.nhnacademy.illuwa.domain.order.factory.strategy;

import java.math.BigDecimal;
import java.util.Optional;

@FunctionalInterface
public interface ItemPriceProvider {
    /**
     * @param bookId 도서 PK
     * @param quantity 주문 수량
     * @param couponId 아이템 쿠폰 (없으면 null)
     * @param payload Cart API가 넘겨준 단가/총액 정보(Direct 주문이면 Optional.empty())
     * @return 단가/총액 정보
    */

    ItemPrice fetchPrice(Long bookId, int quantity, Long couponId, Optional<CartPayload> payload);

    /**
     * @param unitPrice 단가
     * @param grossPrice 단가*수량 (할인 전)
     * @param amountAfterDiscount 할인 액
    */
    record ItemPrice(BigDecimal unitPrice,
                     BigDecimal grossPrice,
                     BigDecimal amountAfterDiscount) {

        /// @return 할인이 적용된 금액
        public BigDecimal netPrice() {
            return grossPrice.subtract(amountAfterDiscount);
        }
    }

    // Cart API가 내려주는 단가·수량·총액 정보를 담는 DTO.
    record CartPayload(BigDecimal unitPrice, int quantity, BigDecimal totalPrice) {}
}
