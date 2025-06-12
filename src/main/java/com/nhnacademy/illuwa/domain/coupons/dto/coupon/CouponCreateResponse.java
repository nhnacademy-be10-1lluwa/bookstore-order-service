package com.nhnacademy.illuwa.domain.coupons.dto.coupon;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CouponCreateResponse {
    // 쿠폰 이름
    private String couponName;

    // 정책 코드 (고유 id)
    private String policyCode;

    private LocalDate validFrom;

    private LocalDate validTo;

    private CouponType couponType;

    private String comment;

    private BigDecimal issueCount;

    // 연동 시 주석해제
//    private Long bookId;
//    private Long categoryId;



    public static CouponCreateResponse fromEntity(Coupon coupon) {
        return CouponCreateResponse.builder()
                .couponName(coupon.getCouponName())
                .policyCode(coupon.getPolicy().getCode())
                .validFrom(coupon.getValidFrom())
                .validTo(coupon.getValidTo())
                .couponType(coupon.getCouponType())
                .comment(coupon.getComment())
                .issueCount(coupon.getIssueCount())
                .build();
    }

}
