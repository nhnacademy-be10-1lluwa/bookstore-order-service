package com.nhnacademy.illuwa.domain.order.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/order/admin/batches")
@RequiredArgsConstructor
@Tag(name = "Batch API", description = "어드민이 직접 배치를 트리거하는 API")
public class BatchTriggerController {
    private final JobLauncher jobLauncher;
    private final Job orderCleanUpJob;
    private final Job memberGradeJob;
    private final Job orderConfirmedJob;

    // 출고일 기준 10일 지난 주문내역들 구매 확정으로 변경
    @Operation(summary = "주문내역 구매 확정 배치 실행", description = "출고일 기준 10일 지난 주문내역건 구매 확정으로 변경하는 배치를 트리거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "요청 수락"),
            @ApiResponse(responseCode = "500", description = "배치 실행 실패")
    })
    @PostMapping("/orders/confirmations")
    public ResponseEntity<String> runOrderConfirmedUpdate() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(orderConfirmedJob, params);
        return ResponseEntity.ok("주문확정 상태 업데이트 실행 결과 = " + execution.getStatus());
    }

    // 멤버별 3개월간 순주문 금액 합계 조회
    @Operation(summary = "순주문 금액 합계 배치 실행", description = "멤벼벌 3개월 간 순주문 금액 합계를 user client로 보내는 배치를 트리거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "요청 수락"),
            @ApiResponse(responseCode = "500", description = "배치 실행 실패")
    })
    @PostMapping("/members/grades")
    public ResponseEntity<String> runMemberGradeUpdate() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(memberGradeJob, params);
        return ResponseEntity.ok("회원 등급 업데이트 실행 결과 = " + execution.getStatus());
    }

    // 3일 간 Awaiting_payment 상태인 주문 내역들 삭제
    @Operation(summary = "주문 삭제 배치 실행", description = "3일 간 결제 대기인 주문내역들을 삭제하는 배치를 트리거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "요청 수락"),
            @ApiResponse(responseCode = "500", description = "배치 실행 실패")
    })
    @PostMapping("/orders/clean-up")
    public ResponseEntity<String> runOrderCleanUp() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(orderCleanUpJob, params);
        return ResponseEntity.ok("주문 테이블 정리 실행 결과 = " + execution.getStatus());
    }

}
