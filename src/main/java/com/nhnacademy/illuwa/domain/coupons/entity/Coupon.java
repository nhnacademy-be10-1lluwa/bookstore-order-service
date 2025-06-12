package com.nhnacademy.illuwa.domain.coupons.entity;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String couponName; // 쿠폰 이름

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private CouponPolicy policy;

    @Column(nullable = false)
    private LocalDate validFrom;
    @Column(nullable = false)
    private LocalDate validTo;

    @Column(nullable = false)
    private CouponType couponType;

    private String comment;

    @Column(nullable = false)
    private BigDecimal issueCount;

    // 특정 도서에만 적용되는 경우
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id")
//    private Book bookTarget;

    // 특정 카테고리에만 적용되는 경우
//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category categoryTarget;

}
