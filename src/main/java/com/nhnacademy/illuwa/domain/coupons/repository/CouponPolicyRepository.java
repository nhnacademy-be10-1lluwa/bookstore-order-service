package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    // 코드로 고유아이디를 가져와서 조회 (=정책 자체를 조회)
    Optional<CouponPolicy> findByCode(String code);

    // 코드 존재여부 확인
    boolean existsByCode(String code);

}
