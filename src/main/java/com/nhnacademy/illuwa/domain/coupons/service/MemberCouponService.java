package com.nhnacademy.illuwa.domain.coupons.service;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponInfoResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MemberCouponService {

    // 회원가입 쿠폰 발급
    MemberCouponResponse issueWelcomeCoupon(String email);

    // 생일 쿠폰 발급
    void issueBirthDayCoupon();
    // 쿠폰 발급
    MemberCouponResponse issueCoupon(MemberCouponCreateRequest request);

    // 발급 내역 조회(테스트 용도)
    MemberCouponResponse getMemberCouponId(Long id);

    List<MemberCouponResponseTest> getAllMemberCouponsTest(Long id);

    // 쿠폰 사용
    MemberCouponUseResponse useCoupon(String email, Long memberCouponId);

    // 발급 내역 조회
    List<MemberCouponResponse> getAllMemberCoupons(String email);

    List<MemberCouponResponse> getAllMemberCoupons(Long memberId);

    Optional<CouponResponse> getCoupon(Long couponId);

    CouponInfoResponse getCouponInfoFromMemberCoupon(Long memberCouponId);

    // 쿠폰의 할인율 or 할인 금액 조회
    MemberCouponDiscountDto getDiscountPrice(Long memberCouponId);
}
