package com.nhnacademy.illuwa.domain.coupon.service;

import com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
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

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberCouponServiceImplTest {
    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCouponService memberCouponService;

    private MemberCouponCreateRequest request;


    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder()
                .name("최정환")
                .birth(LocalDate.parse("1999-08-12"))
                .email("junghwan__@naver.com")
                .build());

        request = MemberCouponCreateRequest.builder()
                .couponName("테 스 트 쿠 폰 이 름 임")
                .memberEmail(member.getEmail())
                .build();
    }

    @Test
    @DisplayName("멤버 쿠폰 발급 (성공)")
    void issueCoupon() {
        Coupon coupon = CouponPolicyTestUtils.createCoupon();
        MemberCouponResponse response = memberCouponService.issueCoupon(request);


    }
}
