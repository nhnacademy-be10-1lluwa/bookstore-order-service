package com.nhnacademy.illuwa.domain.order.dto.order.common;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@AllArgsConstructor
@Getter
public abstract class BaseOrderRequest {
    private LocalDate deliveryDate;
    private String recipientName;
    private String recipientContact;
    private String readAddress;
    private String detailAddress;
}
