package com.nhnacademy.illuwa.domain.coupons.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public org.springframework.amqp.core.Queue welcomeCouponQueue() {
        return new Queue("welcome_coupon_queue", true);
    }

}
