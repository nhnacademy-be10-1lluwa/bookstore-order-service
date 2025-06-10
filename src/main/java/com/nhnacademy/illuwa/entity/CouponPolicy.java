package com.nhnacademy.illuwa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 30)
    private String code; // 고유 id 식별 x -> 코드로 식별o

    private int minOrderAmount; // 최소 주문 금액
    private Integer discountAmount; // 할인 금액
    private Integer discountPercent; // 할인 퍼센트
    private Integer maxDiscountAmount; // 최대 할인 금액
}
