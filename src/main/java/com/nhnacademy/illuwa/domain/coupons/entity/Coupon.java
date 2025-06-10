package com.nhnacademy.illuwa.domain.coupons.entity;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import jakarta.persistence.*;
import lombok.*;

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

    private String couponName; // 쿠폰 이름

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private CouponPolicy policy;

    private LocalDate validFrom;
    private LocalDate validTo;

    private CouponType couponType;

    private String comment;
    // 특정 도서에만 적용되는 경우
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id")
//    private Book bookTarget;

    // 특정 카테고리에만 적용되는 경우
//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category categoryTarget;

}
