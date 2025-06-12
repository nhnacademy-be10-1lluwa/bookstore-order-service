package com.nhnacademy.illuwa.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "shipping_policies")
@RequiredArgsConstructor
public class ShippingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shippingPolicyId;

    @Setter
    private BigDecimal minAmount; // 무료 배송 기준

    @Setter
    private BigDecimal fee; // 기본 배송비

    @Setter
    private boolean isActive; // 활성화 여부

    @Builder
    public ShippingPolicy(BigDecimal minAmount, BigDecimal fee, boolean isActive) {
        this.minAmount = minAmount;
        this.fee = fee;
        this.isActive = isActive;
    }
}
