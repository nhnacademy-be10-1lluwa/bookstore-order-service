package com.nhnacademy.illuwa.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class ShippingPolicies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shippingPolicyId;

    @Setter
    private BigDecimal minAmount; // 무료 배송 기준

    @Setter
    private BigDecimal fee; // 기본 배송비

    @Setter
    private boolean isActive; // 활성화 여부

    public ShippingPolicies(BigDecimal minAmount, BigDecimal fee) {
        this.minAmount = minAmount;
        this.fee = fee;
        this.isActive = false;
    }
}
