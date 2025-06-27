package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponInfoResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponseTest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/members/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    // 웰컴 쿠폰 발급 (회원 서비스에서 해당 컨트롤러를 호출)
    @PostMapping(value = "/welcome", params = "email")
    public ResponseEntity<MemberCouponResponse> issueWelcomeCoupon(@RequestParam String email) {
        return ResponseEntity.ok(memberCouponService.issueWelcomeCoupon(email));
    }

    // 쿠폰 발급
    @PostMapping
    public ResponseEntity<MemberCouponResponse> issueCoupon(
            @RequestBody @Valid MemberCouponCreateRequest request) {
        MemberCouponResponse response = memberCouponService.issueCoupon(request);
        return ResponseEntity.ok(response);
    }

    // 쿠폰 조회
    @GetMapping("/{email}")
    public ResponseEntity<List<MemberCouponResponse>> getAllMemberCoupons(@PathVariable String email) {
        List<MemberCouponResponse> responses = memberCouponService.getAllMemberCoupons(email);
        return ResponseEntity.ok(responses);
    }

    // 쿠폰 조회
    @GetMapping(params = "memberId")
    public ResponseEntity<List<MemberCouponResponse>> getAllMemberCouponsTest(@RequestParam Long memberId) {
        List<MemberCouponResponse> responses = memberCouponService.getAllMemberCoupons(memberId);
        return ResponseEntity.ok(responses);
    }

    // 쿠폰 정보 조회
    @GetMapping("/{memberCouponId}/coupon")
    public ResponseEntity<CouponInfoResponse> getCouponInfoFromMemberCoupon(@PathVariable Long memberCouponId) {
        return ResponseEntity.ok(memberCouponService.getCouponInfoFromMemberCoupon(memberCouponId));
    }

    // 쿠폰 사용
    // 프론트 입장에서는 해당 url은 당연히 x
    // PutMapping("/member-coupons/{id}/use)
    // @AuthenticationPrincipal AuthUser authUser -> 이걸로 인증인가를 성공한 회원을 가져올꺼.
    @PutMapping("/{email}/{memberCouponId}/use")
    public ResponseEntity<MemberCouponUseResponse> useCoupon(@PathVariable String email,
                                                             @PathVariable Long memberCouponId) {

        return ResponseEntity.ok(memberCouponService.useCoupon(email, memberCouponId));
    }


}
