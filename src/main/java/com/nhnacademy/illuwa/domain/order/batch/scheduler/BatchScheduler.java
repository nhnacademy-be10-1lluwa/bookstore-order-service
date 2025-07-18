package com.nhnacademy.illuwa.domain.order.batch.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job orderCleanUpJob;
    private final Job memberGradeJob;

    // 매일 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void launchCleanUp() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(orderCleanUpJob, params);
        log.info("OrderCleanUpJob 실행 결과 = {}", execution.getExitStatus());
    }

    @Scheduled(cron = "0 0 10 1 * ?", zone = "Asia/Seoul")
    @Transactional
    public void launchMemberGradeJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(memberGradeJob, params);
        log.info("memberGradeJob 실행 결과 = {}", execution.getExitStatus());
    }
}
