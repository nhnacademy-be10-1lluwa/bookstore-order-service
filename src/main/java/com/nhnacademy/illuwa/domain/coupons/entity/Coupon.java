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

    @Column(nullable = false, unique = true)
    private String couponName; // 쿠폰 이름

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private CouponPolicy policy; // 정책

    @Column(nullable = false)
    private LocalDate validFrom; // 유효 시작
    @Column(nullable = false)
    private LocalDate validTo; // 유효 종료

    @Column(nullable = false)
    private CouponType couponType; // 쿠폰 타입

    private String comment; // 쿠폰 설명

    private String conditions; // 사용 조건

    @Column(nullable = false)
    private BigDecimal issueCount; // 쿠폰 갯수

    // 특정 도서에만 적용되는 경우
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id")
    @Column(name = "book_id")
    private Long bookId;

    // 특정 카테고리에만 적용되는 경우
//    @ManyToOne
//    @JoinColumn(name = "category_id")
    @Column(name = "category_id")
    private Long categoryId;

}
