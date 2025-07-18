package com.nhnacademy.illuwa.domain.order.batch.config;

import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class OrderCleanUpJobConfig {

    private final JobRepository jobRepository;
    private final EntityManagerFactory em;
    private final DataSource dataSource;

    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Job orderCleanUpJob(PlatformTransactionManager transactionManager) {
        return new JobBuilder("orderCleanUpJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteAwaitingPaymentsStep(transactionManager))
                .build();
    }

    @Bean
    public Step deleteAwaitingPaymentsStep(PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteAwaitingPaymentsStep", jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, transactionManager)
                .reader(awaitingPaymentReader())
                .writer(compositeWriter())
                .faultTolerant()
                .retry(LockTimeoutException.class)
                .retryLimit(3)
                .skip(jakarta.persistence.LockTimeoutException.class)
                .skipLimit(100)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Long> awaitingPaymentReader() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(3);
        Map<String, Object> params = Map.of("status", OrderStatus.AwaitingPayment,
                "threshold", threshold);

        return new JpaPagingItemReaderBuilder<Long>().name("awaitingPaymentReader")
                .entityManagerFactory(em)
                .pageSize(CHUNK_SIZE)
                .queryString("""
                        select o.orderId from Order o
                        where o.orderStatus = :status
                        and o.orderDate < :threshold
                        """)
                .parameterValues(params)
                .build();
    }
    @Bean
    public ItemWriter<Long> compositeWriter() {
        // 자식 테이블 삭제
        JdbcBatchItemWriter<Long> itemDeleteWriter =
                new JdbcBatchItemWriterBuilder<Long>()
                        .dataSource(dataSource)
                        .sql("DELETE FROM order_item WHERE order_id = ?")
                        .itemPreparedStatementSetter((id, ps) -> ps.setLong(1, id))
                        .assertUpdates(false)
                        .build();

        // 부모 테이블 데이터 삭제
        JdbcBatchItemWriter<Long> orderDeleteWriter =
                new JdbcBatchItemWriterBuilder<Long>()
                        .dataSource(dataSource)
                        .sql("DELETE FROM orders WHERE order_id = ?")
                        .itemPreparedStatementSetter((id, ps) -> ps.setLong(1, id))
                        .assertUpdates(false)
                        .build();

        return new CompositeItemWriterBuilder<Long>()
                .delegates(List.of(itemDeleteWriter, orderDeleteWriter))
                .build();
    }
}
