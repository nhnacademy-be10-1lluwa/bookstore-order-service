package com.nhnacademy.illuwa.domain.order.dto.order.guest;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderRequest {
    private String recipientName;
    private String recipientContact;
    private String readAddress;
    private String detailAddress;
    private LocalDate deliveryDate;
    private String cartId;

    private List<CartOrderItemDto> cartItem;
    private BigDecimal totalPrice;

}
