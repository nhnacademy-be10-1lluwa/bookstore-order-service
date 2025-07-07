package com.nhnacademy.illuwa.domain.coupon;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyUpdateResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CouponPolicyTestUtils {
    // 조회 전용 (= 공통 Response 사용)
    public static void assertCouponPolicyEquals(CouponPolicy expected, CouponPolicyResponse actual) {
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getMinOrderAmount()).isEqualByComparingTo(expected.getMinOrderAmount());
        assertThat(actual.getCreateAt()).isEqualTo(expected.getCreateAt());

        if (Objects.nonNull(expected.getDiscountAmount())) {
            assertThat(actual.getDiscountAmount()).isEqualByComparingTo(expected.getDiscountAmount());
        } else {
            assertThat(actual.getDiscountAmount()).isNull();
        }

        if (Objects.nonNull(expected.getDiscountPercent())) {
            assertThat(actual.getDiscountPercent()).isEqualByComparingTo(expected.getDiscountPercent());
        } else {
            assertThat(actual.getDiscountPercent()).isNull();
        }

        if (Objects.nonNull(expected.getMaxDiscountAmount())) {
            assertThat(actual.getMaxDiscountAmount()).isEqualByComparingTo(expected.getMaxDiscountAmount());
        } else {
            assertThat(actual.getMaxDiscountAmount()).isNull();
        }

        if (Objects.nonNull(expected.getUpdateAt())) {
            assertThat(actual.getUpdateAt()).isEqualTo(expected.getUpdateAt());
        } else {
            assertThat(actual.getUpdateAt()).isNull();
        }
    }

    // 생성 전용 (= CreateResponse 사용)
    public static void assertCouponPolicyCreateEquals(CouponPolicy expected, CouponPolicyCreateResponse actual) {
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getMinOrderAmount()).isEqualByComparingTo(expected.getMinOrderAmount());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreateAt());
        if (Objects.nonNull(expected.getDiscountAmount())) {
            assertThat(actual.getDiscountAmount()).isEqualByComparingTo(expected.getDiscountAmount());
        } else {
            assertThat(actual.getDiscountAmount()).isNull();
        }

        if (Objects.nonNull(expected.getDiscountPercent())) {
            assertThat(actual.getDiscountPercent()).isEqualByComparingTo(expected.getDiscountPercent());
        } else {
            assertThat(actual.getDiscountPercent()).isNull();
        }

        if (Objects.nonNull(expected.getMaxDiscountAmount())) {
            assertThat(actual.getMaxDiscountAmount()).isEqualByComparingTo(expected.getMaxDiscountAmount());
        } else {
            assertThat(actual.getMaxDiscountAmount()).isNull();
        }
    }




    // 업데이트 전용 (= UpdateResponse 사용)
    public static void assertCouponPolicyUpdateEquals(CouponPolicy expected, CouponPolicyUpdateResponse actual) {
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getMinOrderAmount()).isEqualByComparingTo(expected.getMinOrderAmount());

        if (Objects.nonNull(expected.getDiscountAmount())) {
            assertThat(actual.getDiscountAmount()).isEqualByComparingTo(expected.getDiscountAmount());
        } else {
            assertThat(actual.getDiscountAmount()).isNull();
        }

        if (Objects.nonNull(expected.getDiscountPercent())) {
            assertThat(actual.getDiscountPercent()).isEqualByComparingTo(expected.getDiscountPercent());
        } else {
            assertThat(actual.getDiscountPercent()).isNull();
        }

        if (Objects.nonNull(expected.getMaxDiscountAmount())) {
            assertThat(actual.getMaxDiscountAmount()).isEqualByComparingTo(expected.getMaxDiscountAmount());
        } else {
            assertThat(actual.getMaxDiscountAmount()).isNull();
        }

        if (Objects.nonNull(expected.getUpdateAt())) {
            assertThat(actual.getUpdatedAt()).isEqualTo(expected.getUpdateAt());
        } else {
            assertThat(actual.getUpdatedAt()).isNull();
        }
    }

    public static CouponPolicy createPolicy() {
        return CouponPolicy.builder()
                .code("testCode")
                .minOrderAmount(BigDecimal.valueOf(20_000))
                .discountType(DiscountType.AMOUNT)
                .discountAmount(BigDecimal.valueOf(3_000))
                .build();

    }

    // 정책 생성 req
    public static CouponPolicyCreateRequest createPolicyRequest() {
        return CouponPolicyCreateRequest.builder()
                .code("testCode")
                .minOrderAmount(BigDecimal.valueOf(20_000))
                .discountAmount(BigDecimal.valueOf(3_000))
                .discountType(DiscountType.AMOUNT)
                .build();
    }

    // 정책 생성 res
    public static CouponPolicyCreateResponse createPolicyResponse() {
        return CouponPolicyCreateResponse.builder()
                .code(createPolicyRequest().getCode())
                .minOrderAmount(createPolicyRequest().getMinOrderAmount())
                .discountAmount(createPolicyRequest().getDiscountAmount())
                .discountType(createPolicyRequest().getDiscountType())
                .build();
    }

    // 정책 응답 res
    public static CouponPolicyResponse policyResponse() {
        return CouponPolicyResponse.builder()
                .id(1L)
                .code(createPolicyRequest().getCode())
                .minOrderAmount(createPolicyRequest().getMinOrderAmount())
                .discountAmount(createPolicyRequest().getDiscountAmount())
                .build();
    }

    public static Coupon createCoupon() {
        return Coupon.builder()
                .id(1L)
                .couponName("테 스 트 쿠 폰 이 름 임")
                .policy(createPolicy())
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusYears(1L))
                .couponType(CouponType.WELCOME)
                .comment("회원가입시 쿠폰 드림 -> 100명 선착순")
                .issueCount(BigDecimal.valueOf(100))
                .build();
    }

//    public static Member createMember() {
//        return Member.builder()
//                .id(1L)
//                .name("testName")
//                .email("test@test.com")
//                .birth(LocalDate.now())
//                .build();
//
//    }

//    public static MemberCoupon createMemberCoupon() {
//        return MemberCoupon.builder()
//                .id(1L)
//                .member(createMember())
//                .coupon(createCoupon())
//                .issuedAt(LocalDate.now())
//                .expiresAt(LocalDate.now().plusMonths(1L))
//                .build();
//    }

    public static CouponCreateRequest createCouponRequest() {
        return CouponCreateRequest.builder()
                .couponName(createCoupon().getCouponName())
                .policyCode(createCoupon().getPolicy().getCode())
                .validFrom(createCoupon().getValidFrom())
                .validTo(createCoupon().getValidTo())
                .couponType(createCoupon().getCouponType()) // 일반
                .comment(createCoupon().getComment())
                .issueCount(createCoupon().getIssueCount())
                .build();
    }

//    public static MemberCouponCreateRequest createMemberCouponRequest() {
//        return MemberCouponCreateRequest.builder()
//                .memberEmail("test@test.com")
//                .couponName("테 스 트 쿠 폰 이 름 임")
//                .build();
//    }

//    public static MemberCouponResponse memberCouponResponse() {
//        return MemberCouponResponse.fromEntity(createMemberCoupon());
//    }



    public static CouponCreateResponse createCouponResponse() {
        return CouponCreateResponse.fromEntity(createCoupon());
    }

    public static CouponResponse couponResponse() {
        return CouponResponse.fromEntity(createCoupon());
    }


    // 테코 목록 조회용도
    public static List<Coupon> createCoupons(int count) {
        List<Coupon> coupons = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            coupons.add(
                    Coupon.builder()
                            .id((long) (i + 1))
                            .couponName("couponTestName")
                            .policy(createPolicy())
                            .validFrom(LocalDate.now())
                            .validTo(LocalDate.now().plusDays(1L))
                            .couponType(CouponType.GENERAL)
                            .comment("테스트용도 쿠폰 생성 " + (i + 1))
                            .issueCount(BigDecimal.valueOf(100))
                            .build()
            );
        }
        return coupons;
    }

    public static List<CouponResponse> createCouponResponses(int count) {
        return createCoupons(count).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

}
