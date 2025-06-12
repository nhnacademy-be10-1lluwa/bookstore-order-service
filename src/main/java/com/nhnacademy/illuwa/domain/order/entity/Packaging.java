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
    private long packagingId;

    @Setter
    private String packagingName;

    @Setter
    private BigDecimal packagingPrice;

    @Setter
    private Boolean active;


    @Builder
    public Packaging(String packagingName, BigDecimal packagingPrice, Boolean active) {
        this.packagingName = packagingName;
        this.packagingPrice = packagingPrice;
        this.active = active;
    }
}
