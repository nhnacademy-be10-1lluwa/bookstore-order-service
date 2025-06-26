package com.nhnacademy.illuwa.domain.order.external.cart;


import com.nhnacademy.illuwa.domain.order.external.cart.dto.CartDto;
import com.nhnacademy.illuwa.domain.order.external.cart.dto.CartIdDto;

import java.util.Optional;

public interface CartApiClient {

    // 카드 조회
    Optional<CartDto> getCart(Long memberId);

    // 카트 비우기 요청
    CartIdDto sendRemoveCartItem(Long memberId);
}
