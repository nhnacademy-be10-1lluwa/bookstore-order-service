package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCouponCreateRequest {
//    @NotBlank @Email
//    private String memberEmail;
//    @NotNull
//    private long memberId;

    private String couponCode;

    private String couponName;
}
