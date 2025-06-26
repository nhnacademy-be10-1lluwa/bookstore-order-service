package com.nhnacademy.illuwa.domain.coupons.service;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponseTest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;

import java.util.List;

public interface MemberCouponService {

    // 회원가입 쿠폰 발급
    MemberCouponResponse issueWelcomeCoupon(String email);

    // 쿠폰 발급
    MemberCouponResponse issueCoupon(MemberCouponCreateRequest request);

    // 발급 내역 조회(테스트 용도)
    MemberCouponResponse getMemberCouponId(Long id);

    List<MemberCouponResponseTest> getAllMemberCouponsTest(Long id);

    // 쿠폰 사용
    MemberCouponUseResponse useCoupon(String email, Long memberCouponId);

    // 발급 내역 조회
    List<MemberCouponResponse> getAllMemberCoupons(String email);
}
