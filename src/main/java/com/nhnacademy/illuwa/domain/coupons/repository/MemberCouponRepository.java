package com.nhnacademy.illuwa.domain.coupons.repository;

import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

    // 특정 회원을 기준으로 모든 쿠폰을 조회 (회원이 존재할 때)
    List<MemberCoupon> findMemberById(Long memberId);

    Optional<MemberCoupon> findMemberCouponById(Long id);

    // repo에서 명명규칙 에러도 있다는걸 첨앎 (= 회원 쿠폰 사용
//    Optional<MemberCoupon> findByMember_EmailAndCoupon_(String email, Long memberId);
    Optional<MemberCoupon> findByMember_EmailAndId(String email, Long memberId);

    // 회원 소유 쿠폰을 가져옴 (Email 기준)
    List<MemberCoupon> findMemberCouponByMemberEmail(String memberEmail);

    // 쿠폰 발급여부 Check
    boolean existsByMemberIdAndCouponId(Long memberId, Long couponId);

//    List<MemberCoupon> findMemberByCouponId(Long couponId);
//
//    // 사용 여부로 쿠폰 조회
//    List<MemberCoupon> findByUsed(boolean used);
//
//    // 특정 회원의 사용된/미사용된 쿠폰 조회
//    List<MemberCoupon> findMemberByUsed(Long memberId, boolean used);
//
//    // 특정 회원의 특정 쿠폰 조회
//    Optional<MemberCoupon> findMemberAndCouponById(Long memberId, Long couponId);
//
//    // 회원 이메일로 해당하는 쿠폰 조회 (회원 이메일 기반)
//    List<MemberCoupon> findMemberByEmail(String email);

}
