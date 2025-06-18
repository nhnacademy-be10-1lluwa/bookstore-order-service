package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.IllegalCharsetNameException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CouponServiceImplTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponServiceImpl couponService;


    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    private CouponCreateRequest request1;
    private CouponCreateRequest request2;

    @BeforeEach
    void setup() {
        couponRepository.deleteAll(); // ddl-auto -> update시
        couponPolicyRepository.deleteAll();

        couponPolicyRepository.save(CouponPolicy.builder()
                .code("testCode1")
                .minOrderAmount(BigDecimal.valueOf(10_000))
                .discountAmount(BigDecimal.valueOf(3_000))
                .build());
        couponPolicyRepository.save(CouponPolicy.builder()
                .code("testCode2")
                .minOrderAmount(BigDecimal.valueOf(20_000))
                .discountPercent(BigDecimal.valueOf(20))
                .build());

        request1 = CouponCreateRequest.builder()
                .couponName("테스트이름-1")
                .policyCode("testCode1")
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(10))
                .couponType(CouponType.GENERAL)
                .issueCount(BigDecimal.valueOf(100))
                .build();

        request2 = CouponCreateRequest.builder()
                .couponName("테스트이름-2")
                .policyCode("testCode2")
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(10))
                .couponType(CouponType.WELCOME)
                .issueCount(BigDecimal.valueOf(50))
                .build();
    }

    @Test
    @DisplayName("정책기반 쿠폰 생성 테스트")
    void createCouponTest() {
        // response 응답
        CouponCreateResponse response = couponService.createCoupon(request1);

        // DB에서 가져옴
        List<Coupon> coupons = couponRepository.findByPolicy_Code("testCode1");

        assertThat(coupons).hasSize(1);
        assertThat(coupons.getFirst().getCouponName()).isEqualTo(response.getCouponName());
    }

    @Test
    @DisplayName("정책기반 쿠폰 생성 -> 실패 - 존재하지 않는 코드")
    void createCouponWithInvalidPolicyCodeTest() {
        CouponCreateRequest notCodeRequest = CouponCreateRequest.builder()
                .couponName("abcd")
                .policyCode("abcdefg")
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(10))
                .issueCount(BigDecimal.valueOf(10))
                .build();

        assertThatThrownBy(() -> couponService.createCoupon(notCodeRequest))
                .isInstanceOf(IllegalCharsetNameException.class)
                .hasMessageContaining("해당 정책코드는 존재하지 않습니다.");
    }

    @Test
    @DisplayName("정책기반 쿠폰 생성 -> 실패 - 정책 비활성화(INACTIVE)")
    void createCouponWithInactivePolicyTest() {

        // 비활성화 정책 저장
        CouponPolicy inactivePolicy = CouponPolicy.builder()
                .code("inactiveCode")
                .minOrderAmount(BigDecimal.valueOf(10_000))
                .discountAmount(BigDecimal.valueOf(3_000))
                .status(CouponStatus.INACTIVE) // 비활성화
                .build();

        couponPolicyRepository.save(inactivePolicy);

        // 정책기반 쿠폰 생성 -> 현재 정책은(= 비활성화 상태)
        CouponCreateRequest request = CouponCreateRequest.builder()
                .couponName("테스트이름-3")
                .policyCode("inactiveCode")
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusDays(10))
                .couponType(CouponType.WELCOME)
                .issueCount(BigDecimal.valueOf(50))
                .build();

        assertThatThrownBy(() -> couponService.createCoupon(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠폰 정책 상태가 비활성화이므로 생성 불가합니다.");
    }

    @Test
    @DisplayName("정책기반 쿠폰 단건 조회 실패 -> 존재하지 않는 ID")
    void getCouponByInvalidIdTest() {
        assertThatThrownBy(() -> couponService.getCouponById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 쿠폰입니다");
    }

    @Test
    @DisplayName("정책기반 쿠폰 전체 조회")
    void getAllCouponsTest() {
        couponService.createCoupon(request1);
        couponService.createCoupon(request2);

        List<CouponResponse> coupons = couponService.getAllCoupons();
        assertThat(coupons).hasSize(2);
    }

    @Test
    @DisplayName("정책기반 쿠폰 단건 조회 -> 이름 (Name)")
    void getCouponsByNameTest() {
        couponService.createCoupon(request1);
        List<CouponResponse> result = couponService.getCouponsByName("테스트이름-1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCouponName()).isEqualTo("테스트이름-1");
    }

    @Test
    @DisplayName("정책기반 쿠폰 단건 조회 -> 타입 (Type)")
    void getCouponsByTypeTest() {
        couponService.createCoupon(request2); // 타입: WELCOME
        List<CouponResponse> result = couponService.getCouponsByType(CouponType.WELCOME);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCouponType()).isEqualTo(CouponType.WELCOME);
    }

    @Test
    @DisplayName("정책기반 쿠폰 단건 조회 -> 정책코드 (PolicyCode)")
    void getCouponByPolicyCodeTest() {
        couponService.createCoupon(request1);
        List<CouponResponse> result = couponService.getCouponsByPolicyCode("testCode1");
        assertThat(result.getFirst().getCode()).isEqualTo("testCode1");
    }

    @Test
    @DisplayName("정책기반 쿠폰 수정(Update)")
    void updateCouponTest() {
        couponService.createCoupon(request1);

        Long id = couponRepository.findByCouponName("테스트이름-1")
                .getFirst()
                .getId();

        CouponUpdateRequest updateRequest = CouponUpdateRequest.builder()
                .couponName("업데이트된 쿠폰")
                .comment("테스트 수정")
                .build();

        couponService.updateCoupon(id, updateRequest);

        CouponResponse response = couponService.getCouponById(id);
        assertThat(response.getCouponName()).isEqualTo("업데이트된 쿠폰");
        assertThat(response.getComment()).isEqualTo("테스트 수정");
        assertThat(response.getCode()).isEqualTo("testCode1");
    }

    @Test
    @DisplayName("정책기반 쿠폰 삭제")
    void deleteCouponTest() {
        couponService.createCoupon(request1);
        Long id = couponRepository.findByCouponName("테스트이름-1")
                .getFirst()
                .getId();

        couponService.deleteCoupon(id);

        assertThatThrownBy(() -> couponService.getCouponById(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 쿠폰");
    }

}
