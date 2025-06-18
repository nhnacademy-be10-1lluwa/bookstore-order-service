package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberCouponServiceImplTest {
    @Autowired
    private MemberCouponRepository memberCouponRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private MemberCouponService memberCouponService;

    private Member member;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        // 멤버 저장(매번 새로운 메일)
        member = memberRepository.save(Member.builder()
                .name("최정환")
                .birth(LocalDate.parse("1999-08-12"))
                .email("junghwan__" + System.nanoTime() + "@naver.com")
                .build());

        // 쿠폰 저장
        coupon = couponRepository.save(CouponPolicyTestUtils.createCoupon());
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon_success() {
        // given
        MemberCouponCreateRequest request = MemberCouponCreateRequest.builder()
                .couponName(coupon.getCouponName())
                .memberEmail(member.getEmail())
                .build();

        // when
        MemberCouponResponse response = memberCouponService.issueCoupon(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCouponName()).isEqualTo(coupon.getCouponName());


        // 쿠폰 발급수 1 감소
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(updatedCoupon.getIssueCount())
                .isEqualByComparingTo(coupon.getIssueCount().subtract(BigDecimal.ONE));
    }

    @Test
    @DisplayName("발급 불가: 이미 발급받은 쿠폰")
    void issueCoupon_duplicate() {
        // given
        MemberCouponCreateRequest request = MemberCouponCreateRequest.builder()
                .couponName(coupon.getCouponName())
                .memberEmail(member.getEmail())
                .build();

        // when - 첫 발급(성공)
        memberCouponService.issueCoupon(request);

        // then - 두번째 발급(실패)
        assertThrows(IllegalArgumentException.class,
                () -> memberCouponService.issueCoupon(request),
                "이미 쿠폰을 발급받으셨습니다.");
    }

    @Test
    @DisplayName("발급 불가: 쿠폰 수량 0")
    void issueCoupon_issueCountExhausted() {
        // given - 수량 0 쿠폰 저장
        coupon.setIssueCount(BigDecimal.ZERO);
        couponRepository.save(coupon);

        MemberCouponCreateRequest request = MemberCouponCreateRequest.builder()
                .couponName(coupon.getCouponName())
                .memberEmail(member.getEmail())
                .build();

        // then
        assertThrows(IllegalArgumentException.class, () ->
                        memberCouponService.issueCoupon(request),
                "발급 가능한 쿠폰 수량이 마감 되었습니다.");
    }

    @Test
    @DisplayName("발급 불가: 존재하지 않는 회원")
    void issueCoupon_memberNotExist() {
        MemberCouponCreateRequest req = MemberCouponCreateRequest.builder()
                .couponName(coupon.getCouponName())
                .memberEmail("NotExistEmail@sample.com")
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> memberCouponService.issueCoupon(req),
                "해당 회원은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("발급 불가: 존재하지 않는 쿠폰")
    void issueCoupon_couponNotExist() {
        MemberCouponCreateRequest req = MemberCouponCreateRequest.builder()
                .couponName("존재하지_않음_쿠폰")
                .memberEmail(member.getEmail())
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> memberCouponService.issueCoupon(req),
                "해당 쿠폰은 존재하지 않습니다.");
    }
}