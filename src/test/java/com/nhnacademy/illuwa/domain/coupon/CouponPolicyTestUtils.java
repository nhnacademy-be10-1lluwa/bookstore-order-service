package com.nhnacademy.illuwa.domain.coupon;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyUpdateResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;

import java.math.BigDecimal;
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

    // 생성 req
    public static CouponPolicyCreateRequest createRequest() {
        return CouponPolicyCreateRequest.builder()
                .code("testCode")
                .minOrderAmount(BigDecimal.valueOf(20_000))
                .discountAmount(BigDecimal.valueOf(3_000))
                .build();
    }

    // 생성 응답 res
    public static CouponPolicyCreateResponse createResponse() {
        return CouponPolicyCreateResponse.builder()
                .code(createRequest().getCode())
                .minOrderAmount(createRequest().getMinOrderAmount())
                .discountAmount(createRequest().getDiscountAmount())
                .build();
    }

    // 기본 응답 res
    public static CouponPolicyResponse response() {
        return CouponPolicyResponse.builder()
                .id(1L)
                .code(createRequest().getCode())
                .minOrderAmount(createRequest().getMinOrderAmount())
                .discountAmount(createRequest().getDiscountAmount())
                .build();
    }
}
