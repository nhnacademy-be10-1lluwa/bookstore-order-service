package com.nhnacademy.illuwa.domain.order.external.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BookPriceDto {
    @JsonProperty("id")
    private Long booKId;
    private BigDecimal priceStandard;
    private BigDecimal priceSales;
}
