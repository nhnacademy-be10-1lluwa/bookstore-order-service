package com.nhnacademy.illuwa.domain.coupons.service;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;

import java.util.List;

public interface MemberCouponService {

    // 쿠폰 발급
    MemberCouponResponse issueCoupon(Long memberId, MemberCouponCreateRequest request);
    // 생일 쿠폰 발급(스케쥴러)
    void issueBirthDayCoupon();

    // 모든 도서 쿠폰 조회
    List<MemberCouponDto> getAvailableCouponsAll(Long memberId);

    // 특정 도서 쿠폰 조회
    List<MemberCouponDto> getAvailableCouponsForBook(Long memberId, Long bookId, CouponType couponType);

    // 특정 카테고리 쿠폰 조회
    List<MemberCouponDto> getAvailableCouponsForCategory(Long memberId, Long categoryId, CouponType couponType);

    // 쿠폰 사용
    MemberCouponUseResponse useCoupon(Long memberId, Long memberCouponId);

     // MemberID를 기준으로 소유중인 쿠폰 확인
    List<MemberCouponResponse> getAllMemberCoupons(Long memberId);

    // 쿠폰의 할인율 or 할인 금액 조회
    MemberCouponDiscountDto getDiscountPrice(Long memberCouponId);
}
