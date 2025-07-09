package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    // (관리자용 -> 정책기반) 쿠폰 단건 조회 {id}
    // 프론트쪽에서는 쿠폰의 목록을 보고 관리자가 기존에 쿠폰 정보를 보고 수정할수 있겠지만
    // 백엔드 입장에서는 기존의 쿠폰 정보를 보려면 /coupons/{@@@}이라는 url 요청을 한 번 해야지 확인이 가능
    // 백엔드 관리자 입장에서도 편의성을 위해 따로 추가
    // ID로 한 이유 -> 클라이언트 입장에서는 굳이 생성된 고유 ID 필드를 몰라도 되기때문에
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long id) {
        CouponResponse response = couponService.getCouponById(id);
        return ResponseEntity.ok(response);
    }

    // (관리자용 -> 정책기반) 쿠폰 생성
    @PostMapping
    public ResponseEntity<CouponCreateResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request) {
        CouponCreateResponse response = couponService.createCoupon(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // (관리자용 -> 정책기반) 쿠폰 수정
    @PutMapping("{id}")
    public ResponseEntity<CouponUpdateResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody @Valid CouponUpdateRequest request) {
        CouponUpdateResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    // (관리자용 -> 정책기반) 쿠폰 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
