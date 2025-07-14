package com.nhnacademy.illuwa.common.external.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCartResponse {
    private Long bookId;
    private String title;
    private int amount;

    private BigDecimal salePrice;
}
