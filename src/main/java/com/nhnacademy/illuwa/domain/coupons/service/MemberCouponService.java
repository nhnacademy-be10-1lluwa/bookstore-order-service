package com.nhnacademy.illuwa.domain.coupons.service;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;

import java.util.List;

public interface MemberCouponService {

    // 쿠폰 발급
    MemberCouponResponse issueCoupon(MemberCouponCreateRequest request);

    // 발급 내역 조회(테스트 용도)
    MemberCouponResponse getMemberCouponId(Long id);

    // 쿠폰 사용
    MemberCouponUseResponse useCoupon(String email, Long memberCouponId);

    // 발급 내역 조회
    List<MemberCouponResponse> getAllMemberCoupons(String email);
}
