package com.nhnacademy.illuwa.domain.coupons.external.book;

import com.nhnacademy.illuwa.domain.coupons.external.category.CategoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BookApiClient {
    private final RestTemplate restTemplate;

    @Value("${book-api.url}")
    private String bookApiUrl;

    public BookApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public BookDto getBookById(Long bookId) {
        String url = bookApiUrl + "admin/books/" + bookId;
        try {
            ResponseEntity<BookDto> response = restTemplate.getForEntity(url, BookDto.class);
            BookDto body = response.getBody();
            if (Objects.isNull(body)) {
                return null; // null 반환
            }
            return body;
        } catch (Exception e) {
            throw new IllegalArgumentException("도서 정보 조회 실패 -> " + e.getMessage());
        }
    }

    public CategoryDto getCategoryId(Long categoryId) {
        String url = bookApiUrl + "categories/" + categoryId;
        try {
            ResponseEntity<CategoryDto> response = restTemplate.getForEntity(url, CategoryDto.class);
            CategoryDto body = response.getBody();
            if (Objects.isNull(body)) {
                return null;
            }
            return body;
        } catch (Exception e) {
            throw new IllegalArgumentException("카테고리 정보 조회 실패 -> " + e.getMessage());
        }
    }
}
