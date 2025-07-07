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
@Builder
public class MemberCouponUseResponse {
//    private String memberName;
    private Long memberId;
//    private String email;
    private String couponName;
    private boolean used;
    private LocalDate usedAt;

    public static MemberCouponUseResponse fromEntity(MemberCoupon memberCoupon) {
        return MemberCouponUseResponse.builder()
//                .memberName("memberCoupon.getMember().getName()")
//                .email("memberCoupon.getMember().getEmail()")
                .memberId(memberCoupon.getMemberId())
                .couponName(memberCoupon.getCoupon().getCouponName())
                .used(memberCoupon.isUsed())
                .usedAt(memberCoupon.getUsedAt())
                .build();
    }
}
