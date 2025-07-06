package com.nhnacademy.illuwa.common.external.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderItemDto {
    private Long bookId;
    private Integer quantity;
    private Long packagingId;
    private Long couponId;
}
