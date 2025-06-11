package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberCouponResponse {
    private Long memberCouponId;
    private String memberName;
    private String couponName;
    private String couponCode;
    private boolean used;
    private LocalDate issuedAt;
    private LocalDate expiresAt;
}
