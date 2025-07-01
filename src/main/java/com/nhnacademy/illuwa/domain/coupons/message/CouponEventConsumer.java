package com.nhnacademy.illuwa.domain.coupons.message;

import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventConsumer {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    // RabbitListenner 어노테이션으로 welcome-coupon-queue에 쌓인 메시지를 자동 수신
    // 해당 큐에 들어있는 이메일의 정보로 회원을 찾음
    @RabbitListener(queues = "welcome_coupon_queue")
    public void handleWelcomeCoupon(String email) {
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. -> " + email));

        Coupon coupon = couponRepository.findByCouponType(CouponType.WELCOME)
                .orElseThrow(() -> new CouponNotFoundException("웰컴 쿠폰을 찾을 수 없습니다."));

        memberCouponRepository.save(MemberCoupon.builder()
                .member(member)
                .coupon(coupon)
                .issuedAt(LocalDate.now())
                .build());
        log.info("웰컴쿠폰 발급 성공 -> member =" + member.getEmail());
    }
}
