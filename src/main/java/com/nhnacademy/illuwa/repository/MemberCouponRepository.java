package com.nhnacademy.illuwa.repository;

import com.nhnacademy.illuwa.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    // 특정 회원을 기준으로 모든 쿠폰을 조회 (member 연결시 적용)
//    List<MemberCoupon> findByMember_Email(String email);

    // 사용 여부
    List<MemberCoupon> findByUsed(boolean used);
}
