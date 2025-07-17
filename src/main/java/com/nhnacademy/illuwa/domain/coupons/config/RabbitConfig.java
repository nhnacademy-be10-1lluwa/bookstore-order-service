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
        return new Jackson2JsonMessageConverter(); // user-service에서 보낸 json을 자바 객체로 변환
    }
//
//    @Bean
//    public Queue welcomeQueue() {
//        return new Queue("book1lluwa_welcome_queue", true);
//    }

    /**
     * 최초 웰컴쿠폰이 발급 실패했을 시 1시간씩 텀을 두고
     * 재시도횟수(maxAttempts(?)만큼 시도)
     * 시간은 최초 발급실패시의 시간과 같음
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        // 큐와 연결을 담당하는 ConectionFactory 설정
        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(jackson2JsonMessageConverter()); // JSON 변환기 설정

        factory.setAdviceChain(RetryInterceptorBuilder
                .stateless() // 상태 비저장형으로 설정 (상태 유지 X)
                .maxAttempts(100) // 최대 재시도 횟수
//                .backOffOptions(5000L, 1.0, 5000L) // 테스트 용도(5초 후 재시도)
                .backOffOptions(3_600_000L, 1.0, 3_600_000L) // 1시간 후 재시도
                .build());
        return factory;
    }

}
