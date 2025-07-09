package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Setter
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Setter
    @Column(name = "shipping_fee_deducted")
    private BigDecimal shippingFeeDeducted; // 차감 배송비

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "return_reason")
    private ReturnReason returnReason;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "return_status")
    private ReturnStatus status;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @Builder
    public ReturnRequest(LocalDateTime requestedAt, LocalDateTime returnedAt, BigDecimal shippingFeeDeducted, ReturnReason returnReason, ReturnStatus status, Order order) {
        this.requestedAt = requestedAt;
        this.returnedAt = returnedAt;
        this.shippingFeeDeducted = shippingFeeDeducted;
        this.returnReason = returnReason;
        this.status = status;
        this.order = order;
    }

}
