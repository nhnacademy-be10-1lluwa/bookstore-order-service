package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestCreateResponseDto {

    private LocalDateTime requestedAt;
    private BigDecimal shippingFeeDeducted;
    private long orderId;
}
