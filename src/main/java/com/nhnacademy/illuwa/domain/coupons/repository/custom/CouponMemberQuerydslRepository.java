package com.nhnacademy.illuwa.domain.coupons.repository.custom;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;

import java.util.Optional;

public interface CouponMemberQuerydslRepository {

     // 해당 멤버 쿠폰의 할인금액, 할인율 가져오기
     Optional<MemberCouponDiscountDto> findDtoByMemberCouponId(Long memberCouponId);
}
