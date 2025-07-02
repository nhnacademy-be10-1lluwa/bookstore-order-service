package com.nhnacademy.illuwa.common.external.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BookDto {
    @JsonProperty("id")
    Long bookId;
    String title;
    String author;
}
