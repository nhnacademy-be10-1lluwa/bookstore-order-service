package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;


import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true) // 목록 조회 용도
public class MemberCouponResponse {
    private Long memberCouponId;
    private String memberName;
    private String memberEmail;
    private String couponName;
    private String couponCode;
    private boolean used;
    private LocalDate usedAt;
    private LocalDate issuedAt;
    private LocalDate expiresAt;

    public static MemberCouponResponse fromEntity(MemberCoupon memberCoupon) {
        return MemberCouponResponse.builder()
                .memberCouponId(memberCoupon.getId())
                .memberName(memberCoupon.getMember().getName())
                .memberEmail(memberCoupon.getMember().getEmail())
                .couponName(memberCoupon.getCoupon().getCouponName())
                .couponCode(memberCoupon.getCoupon().getPolicy().getCode())
                .used(memberCoupon.isUsed())
                .usedAt(memberCoupon.getUsedAt())
                .issuedAt(memberCoupon.getIssuedAt())
                .expiresAt(memberCoupon.getExpiresAt())
                .build();
    }

}
