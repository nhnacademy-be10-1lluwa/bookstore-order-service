package com.nhnacademy.illuwa.domain.coupons.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * "회원가입 시 웰컴 쿠폰 발급" 등 특정 이벤트가 발생시
 * 메시지 큐(RabbitMQ)를 통해 이벤트 정보를 발행(Publish)하는 클래스
 * [해당 클래스의 역할]
 * 특정 회원(email)에게 웰컴 쿠폰을 줘야 해! 라는 이벤트 메시지를
 * welcome_coupon_queue라는 이름의 큐로 전송
 */
@Component // 스프링 빈으로 등록 (IoC 컨테이너가 관리)
public class CouponEventPublisher {
    // RabbitMQ로 메시지를 보내기 위한 템플릿(도구) 객체를 선언
    private final RabbitTemplate rabbitTemplate;

    public CouponEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendWelcomeCouponEvent(String email) {
        //  받은 이벤트를 "welcome_coupon_queue"라는 큐에 email 정보를 적재
        rabbitTemplate.convertAndSend("welcome_coupon_queue", email);
    }
}
