package com.nhnacademy.illuwa.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "shipping_policies")
@RequiredArgsConstructor
public class ShippingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_policy_id")
    private long shippingPolicyId;

    @Setter
    @Column(name = "min_amount", nullable = false)
    private BigDecimal minAmount; // 무료 배송 기준

    @Setter
    @Column(name = "fee", nullable = false)
    private BigDecimal fee; // 기본 배송비

    @Setter
    @Column(name = "active", nullable = false)
    private boolean active; // 활성화 여부

    @Builder
    public ShippingPolicy(BigDecimal minAmount, BigDecimal fee, boolean active) {
        this.minAmount = minAmount;
        this.fee = fee;
        this.active = active;
    }
}
