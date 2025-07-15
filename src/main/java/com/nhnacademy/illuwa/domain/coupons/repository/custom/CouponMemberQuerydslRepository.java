package com.nhnacademy.illuwa.domain.coupons.repository.custom;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;

import java.util.List;
import java.util.Optional;

public interface CouponMemberQuerydslRepository {

     // 해당 멤버 쿠폰의 할인금액, 할인율 가져오기
     Optional<MemberCouponDiscountDto> findDtoByMemberCouponId(Long memberCouponId);

     List<MemberCouponDto> findAvailableCouponsWelcome(Long memberId, CouponType couponType);

     List<MemberCouponDto> findAvailableCouponsForBook(Long memberId, Long bookId, CouponType couponType);

     List<MemberCouponDto> findAvailableCouponsForCategory(Long memberId, Long categoryId, CouponType couponType);

}
