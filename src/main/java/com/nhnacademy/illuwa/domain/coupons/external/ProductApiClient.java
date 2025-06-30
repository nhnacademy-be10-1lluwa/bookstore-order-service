package com.nhnacademy.illuwa.domain.coupons.external;

import com.nhnacademy.illuwa.domain.coupons.external.clientDto.BookDto;
import com.nhnacademy.illuwa.domain.coupons.external.clientDto.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service",url = "http://localhost:10304")
public interface ProductApiClient {

    @GetMapping("/admin/books/{bookId}")
    BookDto getBookById(@PathVariable("bookId") Long bookId);

    @GetMapping("/categories/{categoryId}")
    CategoryDto getCategoryById(@PathVariable("categoryId") Long categoryId);
}

