package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    // 특정 회원을 기준으로 모든 쿠폰을 조회 (회원이 존재할 때)
    List<MemberCoupon> findMemberById(Long memberId);

    List<MemberCoupon> findMemberByCouponId(Long couponId);

    // 사용 여부로 쿠폰 조회
    List<MemberCoupon> findByUsed(boolean used);

    // 특정 회원의 사용된/미사용된 쿠폰 조회
    List<MemberCoupon> findMemberByUsed(Long memberId, boolean used);

    // 특정 회원의 특정 쿠폰 조회
    Optional<MemberCoupon> findMemberAndCouponById(Long memberId, Long couponId);

    // 회원 이메일로 해당하는 쿠폰 조회 (회원 이메일 기반)
    List<MemberCoupon> findMemberByEmail(String email);

}
