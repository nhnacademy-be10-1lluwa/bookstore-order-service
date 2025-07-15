package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyInactiveException;
import com.nhnacademy.illuwa.domain.coupons.exception.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional // 테스트 후 데이터 자동 롤백


class MemberCouponServiceImplTest {

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;
    @Autowired
    private MemberCouponService memberCouponService;

    private CouponPolicy couponPolicy;

    private Coupon coupon;

    private MemberCouponCreateRequest request;

    @MockBean
    UserApiClient userApiClient;

    private final int thisMonth = LocalDate.now().getMonthValue();

    @BeforeEach
    void setup() {
        memberCouponRepository.deleteAll();
        couponRepository.deleteAll();
        couponPolicyRepository.deleteAll();

        couponPolicy = couponPolicyRepository.save(CouponPolicy.builder()
                .code("TEST_" + System.currentTimeMillis())
                .minOrderAmount(BigDecimal.valueOf(10000))
                .discountType(DiscountType.AMOUNT)
                .discountAmount(BigDecimal.valueOf(3000))
                .build());

        coupon = couponRepository.save(Coupon.builder()
                .couponName("테스트쿠폰")
                .policy(couponPolicy)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusYears(1))
                .couponType(CouponType.GENERAL)
                .issueCount(BigDecimal.valueOf(100))
                .build());

        request = MemberCouponCreateRequest.builder()
                .couponName("테스트쿠폰")
                .build();

    }

    @Test
    @DisplayName("쿠폰 정상 발급")
    void issueCouponTest() {
        // given
        MemberCouponResponse response = memberCouponService.issueCoupon(1L, request);

        assertThat(response.getCouponName()).isEqualTo("테스트쿠폰");
        assertThat(response.getMemberId()).isEqualTo(1L);

        List<MemberCoupon> dbResponse = memberCouponRepository.findMemberCouponsByMemberId(1L);

        assertThat(dbResponse).hasSize(1);
    }

    @Test
    @DisplayName("쿠폰 발급 불가 -> 정책 비활성화")
    void InactiveCouponPolicyIssueTest() {
        // 비활성화 처리
        couponPolicy.setStatus(CouponStatus.INACTIVE);


        assertThatThrownBy(() -> memberCouponService.issueCoupon(1L, request))
                .isInstanceOf(CouponPolicyInactiveException.class)
                .hasMessage("해당 쿠폰은 관리자에 의해 임시적으로 발급이 불가능합니다.");

    }

    @Test
    @DisplayName("쿠폰 발급 불가 -> 수량 마감")
    void quantityFinishTest() {
        // given
        coupon.setIssueCount(BigDecimal.ZERO);

        // when & then
        assertThatThrownBy(() -> memberCouponService.issueCoupon(1L, request))
                .isInstanceOf(MemberCouponQuantityFinishException.class)
                .hasMessage("발급 가능한 쿠폰 수량이 마감 되었습니다.");
    }

    @Test
    @DisplayName("쿠폰 발급 불가 -> 중복 발급")
    void duplicationExceptionTest() {
        // given
        memberCouponService.issueCoupon(1L, request);

        assertThatThrownBy(() -> memberCouponService.issueCoupon(1L, request))
                .isInstanceOf(MemberCouponInactiveException.class)
                .hasMessage("이미 쿠폰을 발급받으셨습니다. -> 테스트쿠폰");
    }

    @Test
    @DisplayName("회원 쿠폰 조회")
    void getAllMemberCouponTest() {
        // given (첫 번째 발급)
        memberCouponService.issueCoupon(1L, request);

        // given (다른 쿠폰 저장)
        couponRepository.save(Coupon.builder()
                .couponName("테스트쿠폰2")
                .policy(couponPolicy)
                .couponType(CouponType.GENERAL)
                .validTo(LocalDate.now())
                .validFrom(LocalDate.now().plusYears(1))
                .issueCount(BigDecimal.valueOf(100))
                .build());

        MemberCouponCreateRequest request1 = MemberCouponCreateRequest.builder()
                .couponName("테스트쿠폰2")
                .build();

        // 다른 쿠폰도 발급
        memberCouponService.issueCoupon(1L, request1);


        // when & then
        List<MemberCouponResponse> response = memberCouponService.getAllMemberCoupons(1L);
        assertThat(response).hasSize(2);
    }

    @Test
    @DisplayName("쿠폰 사용")
    void useMemberCouponTest() {
        // 쿠폰 발급
        MemberCouponResponse beforeUse = memberCouponService.issueCoupon(1L, request);

        // 사용 여부 검사
        assertThat(beforeUse.isUsed()).isFalse();

        // 쿠폰 사용
        MemberCouponUseResponse afterUse = memberCouponService.useCoupon(beforeUse.getMemberId(), beforeUse.getMemberCouponId());

        // 사용 여부 검증
        assertThat(afterUse.isUsed()).isTrue();
    }

    @Test
    @DisplayName("쿠폰 사용 -> 관리자 비활성화")
    void NotFoundCouponTest() {
        // 존재하는 쿠폰 발급
        MemberCouponResponse resultCoupon = memberCouponService.issueCoupon(1L, request);

        // 쿠폰 비활성화
        couponPolicy.setStatus(CouponStatus.INACTIVE);

        assertThatThrownBy(() -> memberCouponService.useCoupon(resultCoupon.getMemberId(), resultCoupon.getMemberCouponId()))
                .isInstanceOf(CouponPolicyInactiveException.class)
                .hasMessage("관리자에 의해 정책이 비활성화이므로 사용이 불가능합니다.");
    }

    @Test
    @DisplayName("쿠폰 사용 -> (재사용 불가)")
    void isUsedTest() {
        // 쿠폰 발급
        MemberCouponResponse beforeUse = memberCouponService.issueCoupon(1L, request);
        // 쿠폰 사용
        memberCouponService.useCoupon(beforeUse.getMemberId(), beforeUse.getMemberCouponId());
        // 쿠폰 재사용
        assertThatThrownBy(() -> memberCouponService.useCoupon(beforeUse.getMemberId(), beforeUse.getMemberCouponId()))
                .isInstanceOf(MemberCouponIsUsed.class)
                .hasMessage("이미 사용한 쿠폰입니다.");
    }

    @Test
    @DisplayName("쿠폰 사용 -> (유효기간 만료)")
    void expiresTest() {
        // 쿠폰 발급
        MemberCouponResponse mr = memberCouponService.issueCoupon(1L, request);

        // 쿠폰 유효기간 재설정
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndId(mr.getMemberId(), mr.getMemberCouponId())
                .orElseThrow(() -> new CouponNotFoundException("해당 쿠폰은 존재하지 않습니다."));

        memberCoupon.setExpiresAt(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> memberCouponService.useCoupon(mr.getMemberId(), mr.getMemberCouponId()))
                .isInstanceOf(MemberCouponExpiredException.class)
                .hasMessage("쿠폰의 유효기간이 만료되었습니다.");
    }

    @Test
    @DisplayName("생일 쿠폰 발급 -> 해당 월 생일자가 없으면 예외")
    void memberNotFoundBirthdayTest() {
        Mockito.when(userApiClient.getBirthDayMember(thisMonth))
                .thenReturn(List.of());

        assertThatThrownBy(() -> memberCouponService.issueBirthDayCoupon())
                .isInstanceOf(MemberNotBirthdayMonthException.class)
                .hasMessage("당월 생일자에게만 발급되는 쿠폰입니다.");
    }

    @Test
    @DisplayName("생일 쿠폰 발급 -> 생일 쿠폰 존재 X")
    void birthdayCouponNotFoundTest() {

    }
}
