package com.nhnacademy.illuwa.domain.coupons.dto.coupon;

import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUpdateResponse {
    private String couponName;
    private LocalDate validFrom;
    private LocalDate validTo;
    private CouponType couponType;
    private String comment;
    private BigDecimal issueCount;

    public static CouponUpdateResponse fromEntity(Coupon coupon) {
        return CouponUpdateResponse.builder()
                .couponName(coupon.getCouponName())
                .validFrom(coupon.getValidFrom())
                .validTo(coupon.getValidTo())
                .couponType(coupon.getCouponType())
                .comment(coupon.getComment())
                .issueCount(coupon.getIssueCount())
                .build();
    }
}
