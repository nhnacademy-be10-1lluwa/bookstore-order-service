package com.nhnacademy.illuwa.domain.order.batch.config;

import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.batch.domain.MemberNetOrderAgg;
import com.nhnacademy.illuwa.domain.order.batch.processor.MemberGradeProcessor;
import com.nhnacademy.illuwa.domain.order.batch.writer.MemberGradeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.rmi.RemoteException;

@Configuration
@RequiredArgsConstructor
public class MemberGradeJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager txn;

    private final JpaPagingItemReader<MemberNetOrderAgg> reader;
    private final MemberGradeProcessor processor;
    private final MemberGradeWriter writer;

    @Bean
    public Job memberGradeJob() {
        return new JobBuilder("memberGradeJob", jobRepository)
                .start(memberGradeStep())
                .build();
    }

    @Bean
    public Step memberGradeStep() {
        return new StepBuilder("memberGradeStep", jobRepository)
                .<MemberNetOrderAgg, MemberGradeUpdateRequest>chunk(100, txn)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(RemoteException.class)
                .retryLimit(3)
                .skip(RemoteException.class)
                .skipLimit(100)
                .build();
    }

}
