package com.nhnacademy.illuwa.domain.order.scheduler;

import com.nhnacademy.illuwa.domain.order.external.member.MemberPointApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyNetOrderAmountScheduler {

    private final MemberPointApiClient memberPointApiClient;

    @Scheduled(cron = "0 0 10 1 * ?", zone = "Asia/Seoul")
    public void scheduledSendNetOrderAmount() {
        try {
            int count = memberPointApiClient.sendNetOrderAmount().size();
            log.info("월간 순주문 금액 전송 완료 - {}건", count);
        } catch (Exception e) {
            log.error("월간 순주문 금액 전송 실패 - {}", e.getMessage(), e);
        }
    }
}
