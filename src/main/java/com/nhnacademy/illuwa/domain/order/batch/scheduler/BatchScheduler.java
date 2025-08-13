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

    // 매일 02:00 paymentAwaiting -> 삭제
    @Scheduled(cron = "0 0 2 * * *")
    public void launchCleanUp() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(orderCleanUpJob, params);
        log.info("주문 테이블 정리 실행 결과 = {}", execution.getExitStatus());
    }

    // 매월 1일 10시 멤버 등급 재조정
    @Scheduled(cron = "0 0 10 1 * ?", zone = "Asia/Seoul")
    @Transactional
    public void launchMemberGradeJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(memberGradeJob, params);
        log.info("회원 등급 업데이트 실행 결과 = {}", execution.getExitStatus());
    }

    // 배송일로부터 10일 후 결제확정
    @Scheduled(cron = "0 0 0 * * *")
    public void launchConfirmStatus() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(orderCleanUpJob, params);
        log.info("주문확정 상태 업데이트 실행 결과 = {}", execution.getExitStatus());
    }
}
