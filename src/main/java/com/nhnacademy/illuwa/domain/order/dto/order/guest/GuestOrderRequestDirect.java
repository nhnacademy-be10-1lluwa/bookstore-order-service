package com.nhnacademy.illuwa.domain.order.dto.order.guest;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderRequestDirect {

    private String recipientName;
    private String recipientContact;
    private String readAddress;
    private String detailAddress;
    private LocalDate deliveryDate;

    private CartOrderItemDto item;
}
