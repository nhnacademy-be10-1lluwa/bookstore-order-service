package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MemberCouponDiscountDto {

    private BigDecimal discountAmount;
    private BigDecimal discountPercent;

    @QueryProjection
    public MemberCouponDiscountDto(BigDecimal discountAmount, BigDecimal discountPercent) {
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
    }
}
