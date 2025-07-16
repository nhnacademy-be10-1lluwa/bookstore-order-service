package com.nhnacademy.illuwa.domain.coupons.message;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberDto;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
//import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventConsumer {

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final UserApiClient userApiClient;
    private MemberDto member;
    //
    // RabbitListenner 어노테이션으로 welcome-coupon-queue에 쌓인 메시지를 자동 수신
    // 해당 큐에 들어있는 이메일의 정보로 회원을 찾음
    @RabbitListener(queues = "book1lluwa_welcome_queue", containerFactory = "rabbitListenerContainerFactory")
    public void handleWelcomeCoupon(MemberDto memberDto) {
        try {
            // 해당 memberId로 조회시 존재안하면 바로 예외를 던짐
            member = userApiClient.getMember(memberDto.getMemberId());
        } catch (Exception e) {
            log.error("[웰컴쿠폰발급] 회원 조회 실패 - memberId: {}, message: {}", memberDto.getMemberId(), e.getMessage());
            return;
        }


        try {
            Coupon coupon = couponRepository.findByCouponType(CouponType.WELCOME)
                    .orElseThrow(() -> new CouponNotFoundException("[쿠폰발급] 웰컴 쿠폰이 존재하지 않습니다."));

            memberCouponRepository.save(MemberCoupon.builder()
                    .memberId(member.getMemberId())
                    .coupon(coupon)
                    .issuedAt(LocalDate.now())
                    .build());

            log.info("[웰컴쿠폰] 지급완료 - memberName: {}, memberEmail: {}", member.getName(), member.getEmail());
        } catch (CouponNotFoundException e) {
            log.error("[웰컴쿠폰] 쿠폰 조회 실패: {}", e.getMessage());
            // 명시적으로 에러를 던져서 재시도
            throw e;
        }

    }
}
