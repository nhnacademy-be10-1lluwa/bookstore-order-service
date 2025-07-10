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
public class CouponResponse {
    private Long id;
    private String couponName;
    private String code;
    private LocalDate validFrom;
    private LocalDate validTo;
    private CouponType couponType;
    private String comment;
    private String conditions;
    private BigDecimal issueCount;
    private Long bookId;
    private Long categoryId;


    // 도서와 카테고리 연동시 주석해제
//    private Long bookId;
//    private Long categoryId;

    public static CouponResponse fromEntity(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .couponName(coupon.getCouponName())
                .code(coupon.getPolicy().getCode())
                .validFrom(coupon.getValidFrom())
                .validTo(coupon.getValidTo())
                .couponType(coupon.getCouponType())
                .comment(coupon.getComment())
                .conditions(coupon.getConditions())
                .issueCount(coupon.getIssueCount())
                .bookId(coupon.getBookId())
                .categoryId(coupon.getCategoryId())
                .build();
    }


}
