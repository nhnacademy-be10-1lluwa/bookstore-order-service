package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MemberCouponDto {
    private Long memberCouponId;
    private Long couponId;
    private String couponName;
    private CouponType couponType;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;

    @QueryProjection
    public MemberCouponDto(Long memberCouponId, Long couponId, String couponName,
                           CouponType couponType, BigDecimal discountAmount, BigDecimal discountPercent) {
        this.memberCouponId = memberCouponId;
        this.couponId = couponId;
        this.couponName = couponName;
        this.couponType = couponType;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
    }

}
