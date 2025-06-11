package com.nhnacademy.illuwa.domain.coupons.dto.coupon;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUpdateRequest {
    // 쿠폰 이름 수정
    private String couponName;
    // 유효기간 재설정
    private LocalDate validFrom;
    private LocalDate validTo;
    // 쿠폰 타입 재설정
    private CouponType couponType;
    // 쿠폰 부연설명 재설정
    private String comment;

}
