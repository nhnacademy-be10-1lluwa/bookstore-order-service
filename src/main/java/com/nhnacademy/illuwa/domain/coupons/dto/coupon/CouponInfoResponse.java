package com.nhnacademy.illuwa.domain.coupons.dto.coupon;

import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponInfoResponse {

    private Long couponId;
    private String couponName;
    private CouponType couponType;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscountAmount;
    private BigDecimal issueCount;
    private Long bookId;
    private Long categoryId;

    public static CouponInfoResponse fromEntity(Coupon coupon) {
        return CouponInfoResponse.builder()
                .couponId(coupon.getId())
                .couponName(coupon.getCouponName())
                .couponType(coupon.getCouponType())
                .discountAmount(coupon.getPolicy().getDiscountAmount())
                .discountPercent(coupon.getPolicy().getDiscountPercent())
                .maxDiscountAmount(coupon.getPolicy().getMaxDiscountAmount())
                .issueCount(coupon.getIssueCount())
                .bookId(coupon.getBookId())
                .categoryId(coupon.getCategoryId())
                .build();
    }
}
