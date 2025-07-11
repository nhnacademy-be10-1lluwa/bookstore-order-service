package com.nhnacademy.illuwa.domain.coupons.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue welcomeQueue() {
        return new Queue("1lluwa_welcome_queue", true);
    }

    /**
     * 최초 웰컴쿠폰이 발급 실패했을 시 1시간씩 텀을 두고
     * 재시도횟수(maxAttempts(?)만큼 시도)
     * 시간은 최최 발급실패시의 시간과 같음
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter()); // JSON 변환기 설정
        factory.setAdviceChain(RetryInterceptorBuilder
                .stateless()
                .maxAttempts(100)
//                .backOffOptions(5000L, 1.0, 5000L)
                .backOffOptions(3_600_000L, 1.0, 3_600_000L)
                .build());
        return factory;
    }

}
