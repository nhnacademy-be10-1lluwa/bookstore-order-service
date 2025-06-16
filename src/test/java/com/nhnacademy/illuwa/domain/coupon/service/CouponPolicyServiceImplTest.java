package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.service.impl.CouponPolicyServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class CouponPolicyServiceImplTest {

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;
    @Autowired
    private CouponPolicyServiceImpl couponPolicyService;

    @BeforeEach
    void setup() {
        // 기존 데이터 삭제
        couponPolicyRepository.deleteAll();

        CouponPolicyCreateRequest testRequest1 = CouponPolicyCreateRequest.builder()
                .code("AMT15K_DC3K")
                .minOrderAmount(BigDecimal.valueOf(15000))
                .discountAmount(BigDecimal.valueOf(3000))
                .discountPercent(null)
                .maxDiscountAmount(BigDecimal.valueOf(10000))
                .build();

        CouponPolicyCreateRequest testRequest2 = CouponPolicyCreateRequest.builder()
                .code("AMT20K_DC20P")
                .minOrderAmount(BigDecimal.valueOf(20000))
                .discountAmount(null)
                .discountPercent(BigDecimal.valueOf(20))
                .maxDiscountAmount(BigDecimal.valueOf(10000))
                .build();

        couponPolicyRepository.save(CouponPolicy.builder()
                .code(testRequest1.getCode())
                .minOrderAmount(testRequest1.getMinOrderAmount())
                .discountAmount(testRequest1.getDiscountAmount())
                .discountPercent(testRequest1.getDiscountPercent())
                .maxDiscountAmount(testRequest1.getMaxDiscountAmount())
                .build());

        couponPolicyRepository.save(CouponPolicy.builder()
                .code(testRequest2.getCode())
                .minOrderAmount(testRequest2.getMinOrderAmount())
                .discountAmount(testRequest2.getDiscountAmount())
                .discountPercent(testRequest2.getDiscountPercent())
                .maxDiscountAmount(testRequest2.getMaxDiscountAmount())
                .build());

    }

    @Test
    @DisplayName("쿠폰 정책 생성 테스트")
    void createCouponTest() {
        CouponPolicyCreateRequest request = CouponPolicyCreateRequest.builder()
                .code("AMT30K_DC20P")
                .minOrderAmount(BigDecimal.valueOf(30000))
                .discountAmount(null)
                .discountPercent(BigDecimal.valueOf(20))
                .maxDiscountAmount(BigDecimal.valueOf(10000))
                .build();

        // Response 응답 값 검증
        CouponPolicyCreateResponse response = couponPolicyService.createPolicy(request);

        // 실제 DB에 저장 값 검증
        CouponPolicy savedPolicy = couponPolicyRepository.findByCode(response.getCode()).orElseThrow(() -> new IllegalArgumentException("해당 정책 코드는 존재하지 않습니다."));

        CouponPolicyTestUtils.assertCouponPolicyCreateEquals(savedPolicy,response);
    }

    @Test
    @DisplayName("정책 단건조회 {id} 기준 테스트")
    void getPolicyByIdTest() {
        Long id = couponPolicyRepository.findByCode("AMT15K_DC3K").orElseThrow(() -> new IllegalArgumentException("해당 정책 코드는 존재하지 않습니다.")).getId();
        // @BeforeEach 쪽에 저장해두었던 쿠폰 정책을 가져옴
        // findById(1L)로 지정하여서 @BeforeEach쪽의 정책을 가져오고 싶었지만
        // 어째서인지 클래스단위로 테스트하면 해당 정책을 가져오지 못하고
        // 메서드 단위로 테스트하면 해당 정책을 가져옴
        // 분명 setup() 환경내에 deleteAll()을 통해 분리해서 실행해도 초기화했을텐데
        // -> JPA에서는 ID 값은 자동 증가니까 첫 번째 데이터가 1L이 될수는 없다?
        // -> 그리고 1L처럼 하드코딩은 불안정하다?

        // 실제 DB에 저장되어있던 정책을 ID값을 기준으로 가져옴
        CouponPolicy savedPolicy = couponPolicyRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 정책 ID는 존재하지 않습니다.")
        );

        // 서비스에서 반환된 응답 (DTO)
        CouponPolicyResponse response = couponPolicyService.getPolicyById(savedPolicy.getId());

        // 동일한지 비교
        CouponPolicyTestUtils.assertCouponPolicyEquals(savedPolicy, response);
    }

    @Test
    @DisplayName("정책 단건조회 {code} 기준 테스트")
    void getPolicyByCodeTest() {
        CouponPolicy savedPolicy = couponPolicyRepository.findByCode("AMT15K_DC3K").orElseThrow(
                () -> new IllegalArgumentException("해당 정책 Code는 존재하지 않습니다."));

        CouponPolicyResponse response = couponPolicyService.getPolicyByCode(savedPolicy.getCode());

        CouponPolicyTestUtils.assertCouponPolicyEquals(savedPolicy, response);
    }

    @Test
    @DisplayName("정책 전체조회 테스트")
    void getAllPolicyTest() {
        List<CouponPolicy> savedPolicies = couponPolicyRepository.findAll();
        List<CouponPolicyResponse> responses = couponPolicyService.getAllPolicies();

        // 실 DB || 응답 DTO 크기 검증
        assertEquals(savedPolicies.size(), responses.size());

        // 각각의 DB와 응답 DTO의 각 필드를 정확히 비교
        for (int i = 0; i < savedPolicies.size(); i++) {
            CouponPolicy savedPolicy = savedPolicies.get(i);
            CouponPolicyResponse response = responses.get(i);

            CouponPolicyTestUtils.assertCouponPolicyEquals(savedPolicy, response);
        }
    }

    @Test
    @DisplayName("정책 수정 테스트")
    void updatePolicyTest() {
        CouponPolicy savedPolicy = couponPolicyRepository.findByCode("AMT15K_DC3K")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다."));

        // Builder를 사용하니 전체적인 수정 필요 X
        // 최소주문금액만 수정이 필요하다고 가정
        CouponPolicyUpdateRequest updateRequest = CouponPolicyUpdateRequest.builder()
                .minOrderAmount(BigDecimal.valueOf(30000))
                .build();

        // DB에서 가져온 Code로 해당 정책을 수정
        CouponPolicyUpdateResponse response = couponPolicyService.updatePolicy(savedPolicy.getCode(), updateRequest);

        // 동일한지 비교
        CouponPolicyTestUtils.assertCouponPolicyUpdateEquals(savedPolicy, response);
    }

    @Test
    @DisplayName("정책 삭제 테스트 (= 실제 DB삭제가 아닌 상태 변경)")
    void deletePolicyTest() {
        CouponPolicy savedPolicy = couponPolicyRepository.findByCode("AMT15K_DC3K")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다."));

        // 삭제
        couponPolicyService.deletePolicy(savedPolicy.getCode());

        // 비활성화 상태인지 확인
        assertThat(savedPolicy.getStatus()).isEqualTo(CouponStatus.INACTIVE);

    }
}
