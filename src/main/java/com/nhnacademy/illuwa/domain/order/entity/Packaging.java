package com.nhnacademy.illuwa.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Entity
@NoArgsConstructor
public class Packaging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "packaging_id")
    private long packagingId;

    @Column(name = "packaging_name", nullable = false)
    private String packagingName;

    @Column(name = "packaging_price", nullable = false)
    private BigDecimal packagingPrice;

    @Column(name = "active", nullable = false)
    private Boolean active;


    @Builder
    public Packaging(String packagingName, BigDecimal packagingPrice, Boolean active) {
        this.packagingName = packagingName;
        this.packagingPrice = packagingPrice;
        this.active = active;
    }
}
