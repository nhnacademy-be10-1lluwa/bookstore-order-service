package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class ReturnRequestResponseDto {

    private LocalDateTime requestedAt;
    private LocalDateTime returnedAt;
    private BigDecimal shippingFeeDeducted;
    private ReturnReason reason;
    private ReturnStatus status;
    private long orderId;

    @QueryProjection
    public ReturnRequestResponseDto(LocalDateTime requestedAt, LocalDateTime returnedAt, BigDecimal shippingFeeDeducted, ReturnReason reason, ReturnStatus status, long orderId) {
        this.requestedAt = requestedAt;
        this.returnedAt = returnedAt;
        this.shippingFeeDeducted = shippingFeeDeducted;
        this.reason = reason;
        this.status = status;
        this.orderId = orderId;
    }
}

//