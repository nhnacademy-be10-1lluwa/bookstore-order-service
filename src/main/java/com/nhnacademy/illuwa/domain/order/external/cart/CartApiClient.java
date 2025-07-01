package com.nhnacademy.illuwa.domain.order.external.cart;


import com.nhnacademy.illuwa.domain.order.external.cart.dto.CreateOrderFromCartRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "bookService",
        url = "${book-api.url}"
)
public interface CartApiClient {

    // 카드 조회
    @GetMapping(value = "/members/{memberId}/cart")
    Optional<CreateOrderFromCartRequest> getCart(@PathVariable Long memberId);

    // 카트 비우기 요청
    @DeleteMapping(value = "/members/{memberId}/cart")
    void sendRemoveCartItem(@PathVariable Long memberId);
}
