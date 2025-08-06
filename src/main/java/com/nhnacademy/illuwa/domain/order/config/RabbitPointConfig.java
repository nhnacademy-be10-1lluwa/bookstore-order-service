package com.nhnacademy.illuwa.domain.order.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitPointConfig {
    public static final String POINT_EXCHANGE = "point.exchange";

    @Bean
    public TopicExchange pointExchange() {
        return new TopicExchange(POINT_EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter pointJacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate pointRabbitTemplate(ConnectionFactory connectionFactory,
                                              Jackson2JsonMessageConverter pointJacksonConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(pointJacksonConverter);
        return template;
    }
}
