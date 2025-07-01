package com.nhnacademy.illuwa.domain.coupons.external.clientDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.web.client.RestTemplate;

@Getter
public class BookDto {
    @JsonProperty("id")
    Long bookId;
    String title;
    String author;
}
