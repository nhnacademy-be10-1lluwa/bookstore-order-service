package com.nhnacademy.illuwa.domain.order.dto.order.guest;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.order.dto.order.common.BaseOrderRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GuestOrderRequest extends BaseOrderRequest {

    private List<CartOrderItemDto> cartItem;
    private BigDecimal totalPrice;
}
