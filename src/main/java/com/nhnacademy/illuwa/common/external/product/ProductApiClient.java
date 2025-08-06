package com.nhnacademy.illuwa.common.external.product;


import com.nhnacademy.illuwa.common.external.product.dto.*;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "product-service")
public interface ProductApiClient {


    // 책 가격 가져오기
    @GetMapping(value = "/api/books/{bookId}")
    Optional<BookPriceDto> getBookPriceByBookId(@PathVariable("bookId") Long bookId);

    // 책 정보 가져오기
    @GetMapping("/api/books/{bookId}")
    Optional<BookDto> getBookById(@PathVariable("bookId") Long bookId);


    @GetMapping("/api/admin/books/{bookId}/detail")
    Optional<BookItemOrderDto> getOrderBookById(@PathVariable("bookId") Long bookId);

    // 카테고리 가져오기
    @GetMapping("/api/categories/{categoryId}")
    Optional<CategoryDto> getCategoryById(@PathVariable("categoryId") Long categoryId);


    // 카트 조회 (Member)
    @GetMapping(value = "/api/cart")
    Optional<CartResponse> getCart(@RequestHeader("X-USER-ID") Long memberId);

    // 카트 조회 (Guest)
    @GetMapping(value = "/api/guest/cart/{cartId}")
    Optional<CreateOrderFromCartRequest> getGuestCart(@PathVariable Long cartId);

    // 카트 비우기 요청
    @DeleteMapping(value = "/api/members/{memberId}/cart")
    void sendRemoveCartItem(@PathVariable Long memberId);

    @PutMapping("/api/admin/books/update/bookCount")
    void sendUpdateBooksCount(@RequestHeader("sign") String sign, @RequestBody List<BookCountUpdateRequest> request);

}
