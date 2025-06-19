package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    // 쿠폰 발급
    @PostMapping
    public ResponseEntity<MemberCouponResponse> issueCoupon(
            @RequestBody @Valid MemberCouponCreateRequest request) {
        MemberCouponResponse response = memberCouponService.issueCoupon(request);
        return ResponseEntity.ok(response);
    }
    // 쿠폰 조회
    @GetMapping("/{id}")
    public ResponseEntity<List<MemberCouponResponse>> getAllMemberCoupons(@PathVariable Long id) {
        List<MemberCouponResponse> responses = memberCouponService.getAllMemberCoupons(id);
        return ResponseEntity.ok(responses);
    }
    // 쿠폰 사용
    @PutMapping("/{id}/use")
    public ResponseEntity<MemberCouponUseResponse> useCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(memberCouponService.useCoupon(id));

    }


}
