package com.nhnacademy.illuwa.domain.order.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundDto {
    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;
    private BigDecimal orderStatus;

}
