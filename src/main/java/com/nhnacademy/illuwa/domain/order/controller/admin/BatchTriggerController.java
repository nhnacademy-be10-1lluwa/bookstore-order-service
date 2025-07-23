package com.nhnacademy.illuwa.domain.order.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order/admin/batches")
@RequiredArgsConstructor
public class BatchTriggerController {
    private final JobLauncher jobLauncher;
    private final Job orderCleanUpJob;
    private final Job memberGradeJob;
    private final Job orderConfirmedJob;

    // 출고일 기준 10일 지난 주문내역들 구매 확정으로 변경
    @GetMapping("/orders/confirmations")
    public ResponseEntity<String> runOrderConfirmedUpdate() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(orderConfirmedJob, params);
        return ResponseEntity.ok("주문확정 상태 업데이트 실행 결과 = " + execution.getStatus());
    }

    // 멤버별 3개월간 순주문 금액 합계 조회
    @GetMapping("/members/grades")
    public ResponseEntity<String> runMemberGradeUpdate() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(memberGradeJob, params);
        return ResponseEntity.ok("회원 등급 업데이트 실행 결과 = " + execution.getStatus());
    }

    // 3일 간 Awaiting_payment 상태인 주문 내역들 삭제
    @GetMapping("/orders/clean-up")
    public ResponseEntity<String> runOrderCleanUp() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(orderCleanUpJob, params);
        return ResponseEntity.ok("주문 테이블 정리 실행 결과 = " + execution.getStatus());
    }

}
