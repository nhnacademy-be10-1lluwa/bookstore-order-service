package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.repository.custom.CouponMemberQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, CouponMemberQuerydslRepository {

    // 쿠폰 발급여부 Check(=일반 쿠폰 기준)
    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);

    // 회원의 ID를 기준으로 모든 쿠폰 출력
    List<MemberCoupon> findMemberCouponsByMemberId(Long memberId);
//    // 특정 회원을 기준으로 모든 쿠폰을 조회 (회원이 존재할 때)
//    List<MemberCoupon> findMemberById(Long memberId);
//
//    Optional<MemberCoupon> findMemberCouponById(Long id);
//
    Optional<MemberCoupon> findByMemberIdAndId(Long memberId, Long memberCouponId);
//
//    // 회원 소유 쿠폰을 가져옴 (Email 기준)
//    List<MemberCoupon> findMemberCouponByMemberEmail(String memberEmail);
//
//    // 회원의 ID를 기준으로 모든 쿠폰 출력
//    List<MemberCoupon> findMemberCouponsByMemberId(Long memberId);
//
//    // 쿠폰 발급여부 Check(=일반 쿠폰 기준)
//    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);
//
//    // 쿠폰 발급여부 (= 웰컴 쿠폰)
//    boolean existsByMemberIdAndCoupon_CouponType(Long memberId, CouponType couponCouponType);
//
//    // 쿠폰 ID 정보로 쿠폰 정보 조회
//    Optional<MemberCoupon> findById(Long memberCouponId);

}
