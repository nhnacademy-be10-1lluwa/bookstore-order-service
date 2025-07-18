package com.nhnacademy.illuwa.domain.order.batch.config;

import com.nhnacademy.illuwa.domain.order.batch.domain.MemberNetOrderAgg;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ReaderConfig {

    private final EntityManagerFactory em;
    private static final int CHUNK_SIZE = 100;

    @Bean
    public JpaPagingItemReader<MemberNetOrderAgg> memberNetOrderReader() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(3);

        Map<String, Object> params = Map.of(
                "status", OrderStatus.Confirmed,
                "threshold", threshold);

        return new JpaPagingItemReaderBuilder<MemberNetOrderAgg>()
                .name("memberNetOrderReader")
                .entityManagerFactory(em)
                .pageSize(CHUNK_SIZE)
                .queryString("""
                        SELECT NEW com.example.batch.MemberNetOrderAgg(
                                                       o.memberId,
                                                       SUM(o.totalPrice))
                                              FROM Order o
                                             WHERE o.orderStatus = :status
                                               AND o.orderDate   > :threshold
                                             GROUP BY o.memberId
                        """)
                .parameterValues(params)
                .build();
    }
}
