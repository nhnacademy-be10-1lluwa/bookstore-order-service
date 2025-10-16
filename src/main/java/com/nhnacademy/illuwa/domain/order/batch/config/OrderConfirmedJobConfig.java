package com.nhnacademy.illuwa.domain.order.batch.config;

import com.nhnacademy.illuwa.domain.order.batch.writer.ConfirmAndPointWriter;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.publisher.PointEventPublisher;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OrderConfirmedJobConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory em;

    private final JdbcTemplate jdbcTemplate;
//    private final UserApiClient userApiClient;
    private final PointEventPublisher pointEventPublisher;

    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Job orderConfirmedJob(PlatformTransactionManager transactionManager) {
        return new JobBuilder("orderConfirmedJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updateConfirmedStep(transactionManager))
                .build();
    }

    @Bean
    public Step updateConfirmedStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateConfirmedStep", jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, transactionManager)
                .reader(deliveredReader())
                .writer(confirmWriter())
                .faultTolerant()
                .retry(LockTimeoutException.class)
                .retryLimit(3)
                .skip(LockTimeoutException.class)
                .skipLimit(100)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Long> deliveredReader() {
        LocalDate threshold = LocalDate.now().minusDays(10);
        Map<String, Object> params = Map.of("status", OrderStatus.Shipped.name(),
                "threshold", threshold);

        return new JpaPagingItemReaderBuilder<Long>()
                .name("deliveryReader")
                .entityManagerFactory(em)
                .pageSize(1000)
                .queryString("""
                        select o.orderId
                        from Order o
                        where o.orderStatus = :status
                        and o.deliveryDate < :threshold
                        """)
                .parameterValues(params)
                .build();
    }

    @Bean
    public ItemWriter<Long> confirmWriter() {
        return new ConfirmAndPointWriter(jdbcTemplate, pointEventPublisher);
    }

}
