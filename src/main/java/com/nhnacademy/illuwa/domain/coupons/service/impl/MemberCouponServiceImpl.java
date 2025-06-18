package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;

    // 쿠폰 발급
    @Override
    public MemberCouponResponse issueCoupon(MemberCouponCreateRequest request) {
        Member member = memberRepository.findMemberByEmail(request.getMemberEmail()).orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));
        Coupon coupon = couponRepository.findByCouponName2(request.getCouponName()).orElseThrow(() -> new IllegalArgumentException("해당 쿠폰은 존재하지 않습니다."));

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .used(false)
                .issuedAt(LocalDate.now())
                .build();
        // 만료일 +30 설정
        memberCoupon.setExpireDate();
        // DB저장
        MemberCoupon save = memberCouponRepository.save(memberCoupon);

        return MemberCouponResponse.fromEntity(save);
    }
}
