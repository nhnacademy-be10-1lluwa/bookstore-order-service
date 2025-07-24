package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    // (정책기반) 쿠폰 목록( =PolicyCode로 조회)
    // Query Parameter 방식
    @GetMapping(params = "policyCode")
    public ResponseEntity<List<CouponResponse>> getCouponsByPolicyCode(@RequestParam String policyCode) {
        List<CouponResponse> response = couponService.getCouponsByPolicyCode(policyCode);
        return ResponseEntity.ok(response);
    }


    // (정책기반) 쿠폰 목록 조회 (=Type로 조회)
    // Query Parameter 방식
    @GetMapping(params = "type")
    public ResponseEntity<List<CouponResponse>> getCouponsByType(@RequestParam CouponType type) {
        List<CouponResponse> response = couponService.getCouponsByType(type);
        return ResponseEntity.ok(response);
    }

    // (정책기반) 쿠폰 목록 조회 (=Name로 조회)
    // Query Parameter 방식
    @GetMapping(params = "name")
    public ResponseEntity<List<CouponResponse>> getCouponsByName(@RequestParam String name) {
        List<CouponResponse> response = couponService.getCouponsByName(name);
        return ResponseEntity.ok(response);
    }

    // (정책기반) 쿠폰 전체 조회
    @Operation(
            summary = "쿠폰 전체 목록 조회",
            description = "생성된 쿠폰들을 기준으로 모든 쿠폰 리스트를 보여줍니다.",
            tags = "쿠폰 페이지"
    )
    @ApiResponse(responseCode = "200", description = "정상 응답")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> response = couponService.getAllCoupons();
        return ResponseEntity.ok(response);
    }
}
