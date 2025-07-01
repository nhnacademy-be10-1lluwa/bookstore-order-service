package com.nhnacademy.illuwa.domain.coupons.config;

import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {
    private final MemberCouponService memberCouponService;
    // 초 분 시 일 월 요일
//     @Scheduled(cron = "0 0 0 1 * *") // -> 실 서비스시 매월 1일에만 해당 스케쥴러 가동
//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "*/5 * * * * *") // 테스트 용도 -> 5초마다
    public void autoIssueBirthDayCoupons() {
        memberCouponService.issueBirthDayCoupon();
    }
}
