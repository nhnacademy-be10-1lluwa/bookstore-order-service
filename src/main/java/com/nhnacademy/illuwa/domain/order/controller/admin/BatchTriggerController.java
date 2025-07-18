package com.nhnacademy.illuwa.domain.order.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class BatchTriggerController {
    private final JobLauncher jobLauncher;
    private final Job orderCleanUpJob;
    private final Job memberGradeJob;

    @PostMapping("/order-cleanup")
    public ResponseEntity<String> runOrderCleanUp() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(orderCleanUpJob, params);
        return ResponseEntity.ok("OrderCleanUpJob started. Status = " + execution.getStatus());
    }

    @PostMapping("/member-grade-update")
    public ResponseEntity<String> runMemberGradeUpdate() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(memberGradeJob, params);
        return ResponseEntity.ok("MemberGradeUpdate 시작. 결과 = " + execution.getStatus());
    }
}
