package com.nhnacademy.illuwa.domain.order.service.publisher;

import com.nhnacademy.illuwa.domain.order.dto.event.PointSavedEvent;
import com.nhnacademy.illuwa.domain.order.dto.event.PointUsedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = "point.exchange";
    private static final String ROUTING_KEY_USED = "point.used";
    private static final String ROUTING_KEY_SAVED = "point.saved";

    public void sendPointUsedEvent(PointUsedEvent event) {
        log.info("📤 포인트 사용 이벤트 발행 - memberId={}, usedPoint={}", event.getMemberId(), event.getUsedPoint());
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_USED, event);
    }

    public void sendPointSavedEvent(PointSavedEvent event) {
        log.info("📤 구매확정 시 포인트 적립 이벤트 발행 - memberId={}, price={}", event.getMemberId(), event.getPrice());
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SAVED, event);
    }
}
