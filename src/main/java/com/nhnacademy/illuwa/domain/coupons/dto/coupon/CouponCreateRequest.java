package com.nhnacademy.illuwa.domain.coupons.dto.coupon;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponCreateRequest {
    // 쿠폰 이름
    @NotBlank
    private String couponName;

    // 정책 코드 (고유 id)
    @NotBlank
    private String policyCode;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validTo;

    @NotNull
    private CouponType couponType;

    private String comment;

    private String conditions;

    private String bookName;

    private Long booksId;

    private Long categoryId;

    // 쿠폰 발급수량
    @NotNull
    private BigDecimal issueCount;
    // 연동 시 주석해제
//    private Long bookId;
//    private Long categoryId;


}
