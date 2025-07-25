package com.nhnacademy.illuwa.common.external.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private List<BookCartResponse> bookCarts;
    private BigDecimal totalPrice;
}
