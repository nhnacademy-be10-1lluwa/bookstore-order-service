package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestResponseDto {

    private String memberId;
    private ZonedDateTime requestedAt;
    private ZonedDateTime returnedAt;
    private BigDecimal shippingFeeDeducted;
    private ReturnReason reason;
    private ReturnStatus status;
    private long orderId;
}

//