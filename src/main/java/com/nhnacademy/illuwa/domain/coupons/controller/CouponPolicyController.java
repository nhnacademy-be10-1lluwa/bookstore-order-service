package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;
import com.nhnacademy.illuwa.domain.coupons.service.CouponPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/coupon-policies")
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    @Operation(
            summary = "쿠폰 정책 생성",
            description = "새로운 쿠폰 정책을 생성합니다. (필수: 정책코드(code), 최소주문금액(minOrderAmount), 할인타입(discountType))",
            tags = "관리자 쿠폰 정책"
    )
    @ApiResponse(responseCode = "201", description = "쿠폰 정책 생성 성공")
    @PostMapping
    public ResponseEntity<CouponPolicyCreateResponse> createPolicy(
            @RequestBody @Valid CouponPolicyCreateRequest request) {
        CouponPolicyCreateResponse response = couponPolicyService.createPolicy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "쿠폰 정책 단건 조회",
            description = "쿠폰 정책의 내부 식별자(ID)로 단건을 조회합니다.",
            tags = "관리자 쿠폰 정책"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰 ID 입니다.")
    })

    // 쿠폰 정책 단건 조회 (id)기준
    @GetMapping("/{id}")
    public ResponseEntity<CouponPolicyResponse> getPolicyById(
            @PathVariable Long id) {
        CouponPolicyResponse response = couponPolicyService.getPolicyById(id);
        return ResponseEntity.ok(response);
    }

    // 쿠폰 정책 단건 조회 (code)기준
    // Query Parameter 방식
    @GetMapping(params = "code")
    public ResponseEntity<CouponPolicyResponse> getPolicyByCode(@RequestParam String code) {
        CouponPolicyResponse response = couponPolicyService.getPolicyByCode(code);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "쿠폰 정책 전체 목록 조회",
            description = "등록된 모든 쿠폰 정책을 조회합니다.",
            tags = "관리자 쿠폰 정책"
    )
    @ApiResponse(responseCode = "200", description = "정상 응답")
    // 쿠폰 정책 전체 조회
    @GetMapping
    public ResponseEntity<List<CouponPolicyResponse>> getAllPolicies() {
        List<CouponPolicyResponse> policies = couponPolicyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }



    @Operation(
            summary = "쿠폰 정책 수정",
            description = "쿠폰 정책 코드(code)로 기존 정책 정보를 수정합니다. (RequestBody로 업데이트 정보 전달)",
            tags = "관리자 쿠폰 정책"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰 코드입니다.")
    })
    // 쿠폰 정책 수정
    @PutMapping("/{code}")
    public ResponseEntity<CouponPolicyUpdateResponse> updatePolicy(
            @PathVariable String code,
            @RequestBody CouponPolicyUpdateRequest request) {
        CouponPolicyUpdateResponse response = couponPolicyService.updatePolicy(code, request);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "쿠폰 정책 삭제",
            description = "쿠폰 정책을 소프트 삭제(비활성화)합니다. 실제로는 상태를 INACTIVE로 변경합니다.",
            tags = "관리자 쿠폰 정책"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제(상태변경) 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰 코드입니다.")
    })
    // 쿠폰 정책 삭제(상태 INACTIVE(=비활성화)로 변경)
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String code) {
        couponPolicyService.deletePolicy(code);
        return ResponseEntity.noContent().build();
    }


}