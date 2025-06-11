package com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCouponCreateRequest {
    @NotBlank @Email
    private String memberEmail;
    @NotBlank
    private String couponCode;
}
