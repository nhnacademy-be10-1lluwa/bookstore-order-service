package com.nhnacademy.illuwa.common.external.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookPriceDto {

    // todo Product BigDecimal 변경 되면 BigDecimal로 바꾸기
    @JsonProperty("id")
    private Long bookId;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
}
