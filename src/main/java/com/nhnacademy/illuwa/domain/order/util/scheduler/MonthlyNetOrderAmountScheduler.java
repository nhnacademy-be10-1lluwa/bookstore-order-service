package com.nhnacademy.illuwa.domain.order.util.scheduler;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyNetOrderAmountScheduler {

    private final UserApiClient userApiClient;
    private final OrderRepository orderRepository;


    @Scheduled(cron = "0 0 10 1 * ?", zone = "Asia/Seoul")
    @Transactional
    public void scheduledSendNetOrderAmount() {
        try {
            List<MemberGradeUpdateRequest> request = orderRepository.buildMemberGradeUpdateRequest();
            int count = userApiClient.sendNetOrderAmount(request);
            log.info("월간 순주문 금액 전송 완료 - {}건", count);
        } catch (Exception e) {
            log.error("월간 순주문 금액 전송 실패 - {}", e.getMessage(), e);
        }
    }
}
