package com.nhnacademy.illuwa.domain.coupons.service;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;

public interface MemberCouponService {

    // 쿠폰 발급
    MemberCouponResponse issueCoupon(MemberCouponCreateRequest request);

    // 발급 내역 조회
    MemberCouponResponse getMemberCouponId(Long id);

    // 쿠폰 사용
    MemberCouponUseResponse useCoupon(Long id);


}
