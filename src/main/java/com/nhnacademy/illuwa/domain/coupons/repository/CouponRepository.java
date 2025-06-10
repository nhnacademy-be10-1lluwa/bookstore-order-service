package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // 정책 코드로 쿠폰 조회 (= 해당 정책을 사용하는 쿠폰을 조회)
    Optional<Coupon> findByPolicy_Code(String code);

    // 쿠폰명으로 조회
    // 해당 문제점 -> 중복된 쿠폰명이 포함될수있음
    Optional<Coupon> findByCouponName(String couponName);
}
