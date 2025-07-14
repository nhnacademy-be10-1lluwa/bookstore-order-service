package com.nhnacademy.illuwa.common.external.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderItemDto {
    @JsonProperty("id")
    private Long bookId;
    private Integer quantity;
    private BigDecimal price;
    private Long packagingId;
    private Long couponId;
    private BigDecimal totalPrice;
}
