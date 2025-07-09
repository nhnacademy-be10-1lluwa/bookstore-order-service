package com.nhnacademy.illuwa.common.external.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BookPriceDto {
    @JsonProperty("id")
    private Long bookId;
    private BigDecimal priceStandard;
    private BigDecimal priceSales;
}
