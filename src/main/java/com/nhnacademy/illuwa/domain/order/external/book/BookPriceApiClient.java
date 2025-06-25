package com.nhnacademy.illuwa.domain.order.external.book;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BookPriceApiClient {
    private final RestTemplate restTemplate;

    @Value("${book-api.url}")
    private String bookApiUrl;

    public BookPriceApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Optional<BookPriceDto> getBookPriceByBookId(Long bookId) {
        String url = bookApiUrl + "admin/books?bookId=" + bookId;

        try {
            ResponseEntity<BookPriceDto> response = restTemplate.getForEntity(url, BookPriceDto.class);
            BookPriceDto body = response.getBody();
            return Optional.ofNullable(body);
        } catch (Exception e) {
            throw new IllegalArgumentException("도서 정보 조회 실패 -> " + e.getMessage());
        }
    }
}
