package com.nhnacademy.illuwa.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Packaging(String packagingName, BigDecimal packagingPrice) {
        this.packagingName = packagingName;
        this.packagingPrice = packagingPrice;
    }

}
