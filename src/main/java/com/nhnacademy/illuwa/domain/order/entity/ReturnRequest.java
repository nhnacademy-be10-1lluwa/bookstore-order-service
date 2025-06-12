package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long returnId;

    @Setter
    private ZonedDateTime requestedAt;

    @Setter
    private ZonedDateTime returnedAt;

    @Setter
    private BigDecimal shippingFeeDeducted;

    @Setter
    private ReturnReason returnReason;

    @Setter
    private ReturnStatus status;

    @OneToOne
    private Order order;

    @Builder
    public ReturnRequest(ZonedDateTime requestedAt, ZonedDateTime returnedAt, BigDecimal shippingFeeDeducted, ReturnReason returnReason, ReturnStatus status, Order order) {
        this.requestedAt = requestedAt;
        this.returnedAt = returnedAt;
        this.shippingFeeDeducted = shippingFeeDeducted;
        this.returnReason = returnReason;
        this.status = status;
        this.order = order;
    }
}
