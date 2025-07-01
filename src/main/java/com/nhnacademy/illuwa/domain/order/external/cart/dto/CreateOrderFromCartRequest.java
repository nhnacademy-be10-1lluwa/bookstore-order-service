package com.nhnacademy.illuwa.domain.order.external.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderFromCartRequest {
    private Long cartId;
    private List<CartOrderItemDto> items;
}
