package com.nhnacademy.illuwa.domain.order.util.scheduler;

import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbDataScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanUpAwaitingPayments() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        int deletedOrderItem = orderRepository.deleteItemsBefore(OrderStatus.AwaitingPayment, threeDaysAgo);
        int deletedOrder = orderRepository.deleteByOrderStatusAndOrderDateBefore(
                OrderStatus.AwaitingPayment,
                threeDaysAgo);
        log.info("삭제된 AwaitingPayment 주문 수 = {} \n 삭제된 orderItem 수 = {}", deletedOrder, deletedOrderItem);
    }
}
