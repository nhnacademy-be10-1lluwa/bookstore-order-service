package com.nhnacademy.illuwa.domain.order.external.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(
        name = "bookService",
        url = "${book-api.url}"
)
public interface BookPriceApiClient {

    @GetMapping(value = "/admin/books", params = "bookId")
    Optional<BookPriceDto> getBookPriceByBookId(@RequestParam("bookId") Long bookId);
}
