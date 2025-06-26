package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;

import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class MemberCouponResponseTest {
    private String couponName;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private String condition;
    private String comment;
    private LocalDate expiresAt;
    private CouponType couponType;


    public static MemberCouponResponseTest fromEntity(MemberCoupon memberCoupon) {
        return MemberCouponResponseTest.builder()
                .couponName(memberCoupon.getCoupon().getCouponName())
                .discountAmount(memberCoupon.getCoupon().getPolicy().getDiscountAmount())
                .discountPercent(memberCoupon.getCoupon().getPolicy().getDiscountPercent())
                .condition(memberCoupon.getCoupon().getConditions())
                .expiresAt(memberCoupon.getExpiresAt())
                .couponType(memberCoupon.getCoupon().getCouponType())
                .comment(memberCoupon.getCoupon().getComment())
                .build();
    }
}
