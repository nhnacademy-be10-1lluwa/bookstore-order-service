package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    // (정책기반) 쿠폰 생성
    @PostMapping
    public ResponseEntity<CouponCreateResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request) {
        if (Objects.isNull(request.getBookName())) {
            CouponCreateResponse response = couponService.createCoupon(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            CouponCreateResponse response = couponService.createCouponByBookTitle(request.getBookName(), request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
    }
//
//    @PostMapping
//    public ResponseEntity<CouponCreateResponse> createCouponWithBook(@RequestBody @Valid CouponCreateRequest request) {
//        CouponCreateResponse response = couponService.createCouponByBookTitle(request.getBookName(), request);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    // (정책기반) 쿠폰 단건 조회 {id}
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long id) {
        CouponResponse response = couponService.getCouponById(id);
        return ResponseEntity.ok(response);
    }

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
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> response = couponService.getAllCoupons();
        return ResponseEntity.ok(response);
    }

    // (정책기반) 쿠폰 수정
    @PutMapping("{id}")
    public ResponseEntity<CouponUpdateResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody CouponUpdateRequest request) {
        CouponUpdateResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    // (정책기반) 쿠폰 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
