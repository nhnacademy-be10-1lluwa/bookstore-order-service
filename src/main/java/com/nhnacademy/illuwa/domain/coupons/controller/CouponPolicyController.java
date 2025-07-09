package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;
import com.nhnacademy.illuwa.domain.coupons.service.CouponPolicyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("admin/coupon-policies")
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    // 쿠폰 정책 생성
    @PostMapping
    public ResponseEntity<CouponPolicyCreateResponse> createPolicy(@RequestBody @Valid CouponPolicyCreateRequest request) {
        CouponPolicyCreateResponse response = couponPolicyService.createPolicy(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 쿠폰 정책 단건 조회 (id)기준
    @GetMapping("/{id}")
    public ResponseEntity<CouponPolicyResponse> getPolicyById(@PathVariable Long id) {
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

    // 쿠폰 정책 전체 조회
    @GetMapping
    public ResponseEntity<List<CouponPolicyResponse>> getAllPolicies() {
        List<CouponPolicyResponse> policies = couponPolicyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }



    // 쿠폰 정책 수정
    @PutMapping("/{code}")
    public ResponseEntity<CouponPolicyUpdateResponse> updatePolicy(
            @PathVariable String code,
            @RequestBody CouponPolicyUpdateRequest request) {
        CouponPolicyUpdateResponse response = couponPolicyService.updatePolicy(code, request);
        return ResponseEntity.ok(response);
    }

    // 쿠폰 정책 삭제(상태 INACTIVE(=비활성화)로 변경)
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String code) {
        couponPolicyService.deletePolicy(code);
        return ResponseEntity.noContent().build();
    }

    // 여러 쿠폰 정책 생성
    // @PostMapping("/batch")
    // public ResponseEntity<Void> createCouponPolicies(@RequestBody List<CouponPolicyCreateRequest> policies) {
    //     couponPolicyService.createBatch(policies);
    //     return ResponseEntity.ok().build();
    // }

}