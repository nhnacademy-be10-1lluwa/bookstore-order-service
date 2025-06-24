package com.nhnacademy.illuwa.domain.coupons.external.book;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class BookApiClient {
    private final RestTemplate restTemplate;

    @Value("${book-api.url}")
    private String bookApiUrl;

    public BookApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<BookDto> getBookByTitle(String bookName) {
        String url = bookApiUrl + "admin/books?title=" + bookName;
        System.out.println(url);
        ResponseEntity<BookDto[]> response = restTemplate.getForEntity(url, BookDto[].class);
        return Arrays.asList(response.getBody());
    }
}
