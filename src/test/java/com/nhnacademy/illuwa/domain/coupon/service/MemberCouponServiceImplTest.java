package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.coupons.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class MemberCouponServiceImplTest {

    @Autowired
    private MemberCouponRepository memberCouponRepository;


    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCouponService memberCouponService;

    private CouponPolicy couponPolicy;
    private Coupon coupon;
    private Member member;
    private MemberCouponCreateRequest request;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setup() {
        // 정책 설정
        couponPolicy = couponPolicyRepository.save(CouponPolicyTestUtils.createPolicy());

        // 정책에 의한 쿠폰 설정
        coupon = couponRepository.save(Coupon.builder()
                .couponName("테 스 트 쿠 폰 이 름 임")
                .policy(couponPolicy)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusYears(1L))
                .couponType(CouponType.GENERAL)
                .comment("테스트코드는 반복작업이다.")
                .issueCount(BigDecimal.valueOf(100))
                .build());

        // 멤버가 있다고 가정
        member = memberRepository.save(Member.builder()
                .birth(LocalDate.parse("1999-08-12"))
                .email("junghwan__@naver.com")
                .name("최정환")
                .build());

        // 공통 given
        request = MemberCouponCreateRequest.builder()
                .memberEmail("junghwan__@naver.com")
                .couponName("테 스 트 쿠 폰 이 름 임")
                .build();
    }


    @Test
    @DisplayName("회원 쿠폰 발급 테스트")
    void issueCouponTest() {
        // given (상황)
        MemberCouponCreateRequest issueRequest = MemberCouponCreateRequest.builder()
                .memberEmail("junghwan__@naver.com")
                .couponName("테 스 트 쿠 폰 이 름 임")
                .build();

        // when (행동)
        MemberCouponResponse response = memberCouponService.issueCoupon(issueRequest);

        //than (결과)
        assertThat(response.getCouponName()).isEqualTo("테 스 트 쿠 폰 이 름 임");
        assertThat(memberRepository.count()).isEqualTo(1);
        assertThat(coupon.getIssueCount()).isEqualTo(BigDecimal.valueOf(99));
    }

    @Test
    @DisplayName("회원 쿠폰 발급 예외 테스트 -> 수량 마감")
    void quantityFinishTest() {
        // given
        // 발급 수량을 0으로 설정
        coupon.setIssueCount(BigDecimal.ZERO);

        // when & then
        assertThatThrownBy(() -> memberCouponService.issueCoupon(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급 가능한 쿠폰 수량이 마감 되었습니다.");
    }

    @Test
    @DisplayName("회원 쿠폰 발급 예외 테스트 -> 이미 발급한 쿠폰일때 (중복)")
    void duplicationExceptionTest() {

        // given -> 쿠폰 발급 실행
        memberCouponService.issueCoupon(request);

        // when & then
        assertThatThrownBy(() -> memberCouponService.issueCoupon(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 쿠폰을 발급받으셨습니다.");
    }

    @Test
    @DisplayName("회원 쿠폰 조회")
    void getAllMemberCouponsTest() {
        // 현재 회원을 기준으로 기존 request를 통해 쿠폰하나 발행
        memberCouponService.issueCoupon(request);
        // 기존 정책을 재사용하여 다른 쿠폰을 생성
        Coupon differentCoupon = couponRepository.save(Coupon.builder()
                .couponName("두 번 째 테 스 트 쿠 폰 이 름 임")
                .policy(couponPolicy) // 기존 정책 재사용
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusMonths(6))
                .couponType(CouponType.GENERAL)
                .comment("테스트코드는 반복작업이다. -> 진짜임")
                .issueCount(BigDecimal.valueOf(50))
                .build());
        // 다른 쿠폰도 같이 발급
        MemberCouponCreateRequest issueRequest = MemberCouponCreateRequest.builder()
                .memberEmail("junghwan__@naver.com")
                .couponName("두 번 째 테 스 트 쿠 폰 이 름 임")
                .build();
        memberCouponService.issueCoupon(issueRequest);

        memberCouponService.getMemberCouponId(member.getId());

        assertThat(memberCouponRepository.count()).isEqualTo(2);
    }
}
