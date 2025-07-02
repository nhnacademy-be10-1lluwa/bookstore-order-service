package com.nhnacademy.illuwa.common.external.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderItemDto {
    private Long bookId;
    private int quantity;

}
