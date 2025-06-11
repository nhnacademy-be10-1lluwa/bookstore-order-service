package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.ReturnReason;
import com.nhnacademy.illuwa.domain.order.ReturnStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private ReturnReason reason;

    @Setter
    private ReturnStatus status;

    @OneToOne
    private Order order;

    public ReturnRequest(ZonedDateTime requestedAt, ZonedDateTime returnedAt, BigDecimal shippingFeeDeducted, Order order) {
        this.requestedAt = requestedAt;
        this.returnedAt = returnedAt;
        this.shippingFeeDeducted = shippingFeeDeducted;
        this.order = order;
    }
}
