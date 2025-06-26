package com.nhnacademy.illuwa.domain.coupons.external.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BookDto {
    @JsonProperty("id")
    Long bookId;
    String title;
    String category;
    // 도서 존재 O -> 쿠폰을 생성  (도서1) - book -> 1
    // 주문 -> 주문이 도서1 도서2 도서3 -> 주문 처리를하는데 쿠폰이 활성화 (도서1 -> 도서2) id -2  useCoupon()
}
