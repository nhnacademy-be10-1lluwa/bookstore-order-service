package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "return_request")
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private long returnId;

    @Setter
    private String memberId;

    @Setter
    private ZonedDateTime requestedAt;

    @Setter
    private ZonedDateTime returnedAt;

    @Setter
    private BigDecimal shippingFeeDeducted;

    @Setter
    @Enumerated(EnumType.STRING)
    private ReturnReason returnReason;

    @Setter
    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    @OneToOne
    @JoinColumn(name = "order_order_id", referencedColumnName = "order_id")
    private Order order;

    @Builder
    public ReturnRequest(String memberId, ZonedDateTime requestedAt, ZonedDateTime returnedAt, BigDecimal shippingFeeDeducted, ReturnReason returnReason, ReturnStatus status, Order order) {
        this.memberId = memberId;
        this.requestedAt = requestedAt;
        this.returnedAt = returnedAt;
        this.shippingFeeDeducted = shippingFeeDeducted;
        this.returnReason = returnReason;
        this.status = status;
        this.order = order;
    }
}
