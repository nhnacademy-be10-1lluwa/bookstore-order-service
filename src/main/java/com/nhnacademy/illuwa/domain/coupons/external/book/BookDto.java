package com.nhnacademy.illuwa.domain.coupons.external.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BookDto {
    @JsonProperty("id")
    Long bookId;
    String title;
    String author;
    String category;
}
