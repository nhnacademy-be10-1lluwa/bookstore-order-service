package com.nhnacademy.illuwa.domain.order.dto.order.guest;

import com.nhnacademy.illuwa.common.external.product.dto.OrderItemDto;
import com.nhnacademy.illuwa.domain.order.dto.order.common.BaseOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderRequestDirect extends BaseOrderRequest {

    private String name;
    private String orderPassword;
    private String email;
    private String contact;
    private OrderItemDto item;
}
