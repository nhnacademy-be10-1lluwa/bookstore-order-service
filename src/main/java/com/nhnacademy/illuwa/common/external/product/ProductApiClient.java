package com.nhnacademy.illuwa.common.external.product;


import com.nhnacademy.illuwa.common.external.product.dto.BookDto;
import com.nhnacademy.illuwa.common.external.product.dto.BookPriceDto;
import com.nhnacademy.illuwa.common.external.product.dto.CategoryDto;
import com.nhnacademy.illuwa.common.external.product.dto.CreateOrderFromCartRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(
        name = "product-service"
)
public interface ProductApiClient {


    // 책 가격 가져오기
    @GetMapping(value = "/admin/books", params = "bookId")
    Optional<BookPriceDto> getBookPriceByBookId(@RequestParam("bookId") Long bookId);

    // 책 정보 가져오기
    @GetMapping("/books/{bookId}")
    BookDto getBookById(@PathVariable("bookId") Long bookId);

    // 카테고리 가져오기
    @GetMapping("/categories/{categoryId}")
    CategoryDto getCategoryById(@PathVariable("categoryId") Long categoryId);


    // 카트 조회 (Member)
    @GetMapping(value = "/members/{memberId}/cart")
    Optional<CreateOrderFromCartRequest> getCart(@PathVariable Long memberId);

    // 카트 조회 (Guest)
    @GetMapping(value = "/guest/cart/{cartId}")
    Optional<CreateOrderFromCartRequest> getGuestCart(@PathVariable Long cartId);

    // 카트 비우기 요청
    @DeleteMapping(value = "/members/{memberId}/cart")
    void sendRemoveCartItem(@PathVariable Long memberId);
}
