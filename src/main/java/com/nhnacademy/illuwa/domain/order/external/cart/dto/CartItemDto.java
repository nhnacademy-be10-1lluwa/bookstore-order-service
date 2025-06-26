package com.nhnacademy.illuwa.domain.order.external.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long bookId;
    private int quantity;

}
