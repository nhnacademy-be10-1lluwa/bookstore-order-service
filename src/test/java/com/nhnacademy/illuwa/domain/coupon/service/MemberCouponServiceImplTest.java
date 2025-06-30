package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.memberCoupon.*;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "spring.config.import=classpath:application-db.yml")
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
    @Autowired
    private CouponService couponService;

    @BeforeEach
    void setup() {
        memberCouponRepository.deleteAll();
        couponRepository.deleteAll();
        couponPolicyRepository.deleteAll();
        memberRepository.deleteAll();

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
                .isInstanceOf(MemberCouponQuantityFinishException.class)
                .hasMessage("발급 가능한 쿠폰 수량이 마감 되었습니다.");
    }

    @Test
    @DisplayName("회원 쿠폰 발급 예외 테스트 -> 이미 발급한 쿠폰일때 (중복)")
    void duplicationExceptionTest() {

        // given -> 쿠폰 발급 실행
        memberCouponService.issueCoupon(request);

        // when & then
        assertThatThrownBy(() -> memberCouponService.issueCoupon(request))
                .isInstanceOf(MemberCouponInactiveException.class)
                .hasMessage("이미 쿠폰을 발급받으셨습니다. -> 테 스 트 쿠 폰 이 름 임");
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

        memberCouponService.getAllMemberCoupons(member.getEmail());

        assertThat(memberCouponRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("회원 쿠폰 사용")
    void useMemberCouponTest() {
        // 위와 동일하게 setup 쪽에 미리 정의해놓은 로직으로 쿠폰 발행. -> 아직 사용전임
        MemberCouponResponse beforeUse = memberCouponService.issueCoupon(request);

        // 아직 사용여부가 false 인지 검증
        assertThat(beforeUse.isUsed()).isFalse();

        // 쿠폰 사용
        memberCouponService.useCoupon(beforeUse.getMemberEmail(), beforeUse.getMemberCouponId());

        MemberCouponResponse afterUse = memberCouponService.getMemberCouponId(beforeUse.getMemberCouponId());
        // true로 변경되었는지 검정
        assertThat(afterUse.isUsed()).isTrue();
    }

    @Test
    @DisplayName("회원 쿠폰 사용 (중복사용 방지)")
    void isUsedTest() {
        // 위와 동일하게 setup 쪽에 미리 정의해놓은 로직으로 쿠폰 발행. -> 아직 사용전임
        MemberCouponResponse beforeUse = memberCouponService.issueCoupon(request);

        // 아직 사용여부가 false 인지 검증
        assertThat(beforeUse.isUsed()).isFalse();

        // 쿠폰 사용
        memberCouponService.useCoupon(beforeUse.getMemberEmail(), beforeUse.getMemberCouponId());

        // 한번 더 해당 쿠폰을 사용하려고 하면?
        assertThatThrownBy(() -> memberCouponService.useCoupon(beforeUse.getMemberEmail(), beforeUse.getMemberCouponId()))
                .isInstanceOf(MemberCouponIsUsed.class)
                .hasMessage("이미 사용한 쿠폰입니다.");
    }

    @Test
    @DisplayName("회원 쿠폰 사용 (유효기간 만료)")
    void expiresTest() {
        // 위와 동일하게 setup 쪽에 미리 정의해놓은 로직으로 쿠폰 발행. -> 아직 사용전임
        MemberCouponResponse beforeUse = memberCouponService.issueCoupon(request);

        // 아직 사용여부가 false 인지 검증
        assertThat(beforeUse.isUsed()).isFalse();

        // 유효기간 재설정
        MemberCoupon memberCoupon = memberCouponRepository.findMemberCouponById(beforeUse.getMemberCouponId()).orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다."));
        memberCoupon.setExpiresAt(LocalDate.now().minusDays(1));

        // 유효 기간이 지났다면?
        assertThatThrownBy(() -> memberCouponService.useCoupon(beforeUse.getMemberEmail(), beforeUse.getMemberCouponId()))
                .isInstanceOf(MemberCouponExpiredException.class)
                .hasMessage("쿠폰의 유효기간이 만료되었습니다.");

    }

    @Test
    @DisplayName("WELCOME 쿠폰 발급 (성공)")
    void 웰컴쿠폰_발급_성공() {
        // given
        couponRepository.save(Coupon.builder()
                .id(1L)
                .couponName("웰컴쿠폰")
                .policy(couponPolicy)
                .validFrom(LocalDate.of(2025, 1, 1))
                .validTo(LocalDate.of(2025, 12, 31))
                .couponType(CouponType.WELCOME)
                .comment("회원가입 대상자에게만 발행되는 쿠폰입니다.")
                .conditions("최소주문 금액 20,000이상 시 3,000원 할인")
                .issueCount(BigDecimal.valueOf(100))
                .build());

        // when
        MemberCouponResponse response = memberCouponService.issueWelcomeCoupon("junghwan__@naver.com");

        // then
        assertThat(response.getCouponName()).isEqualTo("웰컴쿠폰");
    }

    @Test
    @DisplayName("WELCOME 쿠폰 발급 (실패 -> 중복발급)")
    void 웰컴쿠폰_중복발급_실패() {
        // given
        couponRepository.save(Coupon.builder()
                .id(1L)
                .couponName("웰컴쿠폰")
                .policy(couponPolicy)
                .validFrom(LocalDate.of(2025, 1, 1))
                .validTo(LocalDate.of(2025, 12, 31))
                .couponType(CouponType.WELCOME)
                .comment("회원가입 대상자에게만 발행되는 쿠폰입니다.")
                .conditions("최소주문 금액 20,000이상 시 3,000원 할인")
                .issueCount(BigDecimal.valueOf(100))
                .build());
        memberCouponService.issueWelcomeCoupon("junghwan__@naver.com");

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> memberCouponService.issueWelcomeCoupon("junghwan__@naver.com"));

        assertThat(exception.getMessage()).isEqualTo("해당 회원은 이미 웰컴 쿠폰을 지급받으셨습니다.");
    }

    @Test
    @DisplayName("WELCOME 쿠폰 발급 (실패 -> 웰컴쿠폰X)")
    void 웰컴쿠폰_존재X_발급실패() {

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> memberCouponService.issueWelcomeCoupon("junghwan__@naver.com"));

        assertThat(exception.getMessage()).isEqualTo("WELCOME 쿠폰이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("WELCOME 쿠폰 발급 (실패 -> 수량마감)")
    void 웰컴쿠폰_수량마감_발급실패() {
        // given
        couponRepository.save(Coupon.builder()
                .id(1L)
                .couponName("웰컴쿠폰")
                .policy(couponPolicy)
                .validFrom(LocalDate.of(2025, 1, 1))
                .validTo(LocalDate.of(2025, 12, 31))
                .couponType(CouponType.WELCOME)
                .comment("회원가입 대상자에게만 발행되는 쿠폰입니다.")
                .conditions("최소주문 금액 20,000이상 시 3,000원 할인")
                .issueCount(BigDecimal.valueOf(100))
                .build());

        Coupon welcomeCoupon = couponRepository.findCouponByCouponName("웰컴쿠폰")
                .orElseThrow(() -> new CouponNotFoundException("웰컴 쿠폰이 존재하지 않습니다."));

        welcomeCoupon.setIssueCount(BigDecimal.ZERO);

        // when & then
        MemberCouponQuantityFinishException exception = assertThrows(MemberCouponQuantityFinishException.class,
                () -> memberCouponService.issueWelcomeCoupon("junghwan__@naver.com"));

        assertThat(exception.getMessage()).isEqualTo("발급 가능한 쿠폰 수량이 마감 되었습니다.");
    }

    @Test
    @DisplayName("생일 쿠폰 발급 (성공)")
    void 생일쿠폰_발급_성공() {
        // given
        member.setBirth(LocalDate.now());

        couponRepository.save(Coupon.builder()
                .id(1L)
                .couponName("생일쿠폰")
                .policy(couponPolicy)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusMonths(3L))
                .couponType(CouponType.BIRTHDAY)
                .comment("생일 축하드립니다.")
                .issueCount(BigDecimal.valueOf(1000))
                .build());

        // when
        memberCouponService.issueBirthDayCoupon();

        // then
        List<MemberCoupon> memberCoupons = memberCouponRepository.findMemberById(member.getId());
        assertThat(memberCoupons).isNotEmpty();
        assertThat(memberCoupons.stream()
                .anyMatch(mc -> mc.getCoupon().getCouponName().equals("생일쿠폰"))).isTrue();

    }

    @Test
    @DisplayName("생일 쿠폰 발급 (실패 -> 당월 생일이 아님)")
    void 생일쿠폰_발급_실패() {
        member.setBirth(LocalDate.of(1999, 8, 12));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> memberCouponService.issueBirthDayCoupon());

        assertThat(exception.getMessage()).isEqualTo("당월 생일자에게만 발급되는 쿠폰입니다.");
    }

    @Test
    @DisplayName("생일 쿠폰 발급 (실패 -> 발급 수량 마감)")
    void 생일쿠폰_발급_실패_수량마감() {
        // given
        member.setBirth(LocalDate.now());

        couponRepository.save(Coupon.builder()
                .id(1L)
                .couponName("생일쿠폰")
                .policy(couponPolicy)
                .validFrom(LocalDate.now())
                .validTo(LocalDate.now().plusMonths(3L))
                .couponType(CouponType.BIRTHDAY)
                .comment("생일 축하드립니다.")
                .issueCount(BigDecimal.ZERO)
                .build());

        Coupon birthDayCoupon = couponRepository.findCouponByCouponName("생일쿠폰")
                .orElseThrow(() -> new MemberCouponQuantityFinishException("발급 가능한 쿠폰 수량이 마감 되었습니다."));


        // when & then
        MemberCouponQuantityFinishException exception = assertThrows(MemberCouponQuantityFinishException.class,
                () -> memberCouponService.issueBirthDayCoupon());

        assertThat(birthDayCoupon.getIssueCount()).isEqualTo(BigDecimal.ZERO);
        assertThat(exception.getMessage()).isEqualTo("발급 가능한 쿠폰 수량이 마감 되었습니다.");
    }

    @Test
    @DisplayName("회원 소유 쿠폰 리스트 확인 {")
    void 회원소유_쿠폰_리스트확인() {
        // given
        memberCouponService.issueCoupon(request); // 쿠폰 1개 발급

        // when
        List<MemberCouponResponse> responses = memberCouponService.getAllMemberCoupons(member.getId());

        // then
        assertThat(responses.size()).isEqualTo(1);
    }
}
