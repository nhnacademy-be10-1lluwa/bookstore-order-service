package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    @Operation(
            summary = "사용자 쿠폰 발급",
            description = "사용자의 내부 식별자(ID)와 쿠폰 발급 요청 정보를 받아 쿠폰을 발급합니다.",
            tags = "사용자 쿠폰"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
            @ApiResponse(responseCode = "409", description = "해당 쿠폰은 관리자에 의해 임시적으로 발급이 불가능합니다."),
            @ApiResponse(responseCode = "409", description = "발급 가능한 쿠폰 수량이 마감 되었습니다."),
            @ApiResponse(responseCode = "409", description = "이미 쿠폰을 발급받으셨습니다."),
            @ApiResponse(responseCode = "409", description = "쿠폰의 발급 가능 기간이 아닙니다.")
    })
    // 쿠폰 발급
    @PostMapping
    public ResponseEntity<MemberCouponResponse> issueCoupon(@RequestHeader("X-USER-ID") Long memberId, @RequestBody @Valid MemberCouponCreateRequest request) {
        MemberCouponResponse response = memberCouponService.issueCoupon(memberId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "사용자 쿠폰 전체 조회",
            description = "사용자 ID로 발급받은 모든 쿠폰을 조회합니다.",
            tags = "사용자 쿠폰"
    )
    @ApiResponse(responseCode = "200", description = "정상 조회")
    // 쿠폰 조회
    @GetMapping
    public ResponseEntity<List<MemberCouponResponse>> getAllMemberCouponsTest(@RequestHeader("X-USER-ID") Long memberId) {
        List<MemberCouponResponse> responses = memberCouponService.getAllMemberCoupons(memberId);
        return ResponseEntity.ok(responses);
    }

//     쿠폰 정보 조회
//    @GetMapping("/{memberCouponId}")
//    public ResponseEntity<CouponInfoResponse> getCouponInfoFromMemberCoupon(@PathVariable Long memberCouponId) {
//        return ResponseEntity.ok(memberCouponService.getCouponInfoFromMemberCoupon(memberCouponId));
//    }


    @Operation(
            summary = "사용자 쿠폰 사용",
            description = "사용자가 보유한 쿠폰을 사용 처리합니다.",
            tags = "사용자 쿠폰"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "쿠폰 사용 성공"),
            @ApiResponse(responseCode = "404", description = "회원이 소유한 쿠폰이 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "관리자에 의해 정책이 비활성화이므로 사용이 불가능합니다."),
            @ApiResponse(responseCode = "409", description = "이미 사용한 쿠폰입니다."),
            @ApiResponse(responseCode = "409", description = "쿠폰의 유효기간이 만료되었습니다."),
    })
    // 쿠폰 사용
    // 프론트 입장에서는 해당 url은 당연히 x
    // PutMapping("/member-coupons/{id}/use)
    // @AuthenticationPrincipal AuthUser authUser -> 이걸로 인증인가를 성공한 회원을 가져올꺼.
    @PutMapping("/{memberCouponId}/use")
    public ResponseEntity<MemberCouponUseResponse> useCoupon(@RequestHeader("X-USER-ID") Long memberId,
                                                             @PathVariable Long memberCouponId) {

        return ResponseEntity.ok(memberCouponService.useCoupon(memberId, memberCouponId));
    }


    @Operation(
            summary = "특정 도서 적용 가능 쿠폰 조회",
            description = "도서ID와 쿠폰타입으로 해당 도서에 사용 가능한 사용자 쿠폰 목록을 조회합니다.",
            tags = "사용자 쿠폰"
    )
    @ApiResponse(responseCode = "200", description = "정상 조회")
    /**
     * 특정 책에 적용 가능한 쿠폰 조회
     * GET /api/member-coupons/book/{bookId}?couponType=BOOKS
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<MemberCouponDto>> getBookCoupons(
            @RequestHeader("X-USER-ID") Long memberId,
            @PathVariable Long bookId,
            @RequestParam CouponType couponType) {
        List<MemberCouponDto> result = memberCouponService.getAvailableCouponsForBook(memberId, bookId, couponType);
        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "특정 카테고리 적용 가능 쿠폰 조회",
            description = "카테고리ID와 쿠폰타입으로 해당 카테고리에 사용 가능한 사용자 쿠폰 목록을 조회합니다.",
            tags = "사용자 쿠폰"
    )
    @ApiResponse(responseCode = "200", description = "정상 조회")
    /**
     * 특정 카테고리에 적용 가능한 쿠폰 조회
     * GET /api/member-coupons/category/{categoryId}?couponType=CATEGORY
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MemberCouponDto>> getCategoryCoupons(
            @RequestHeader("X-USER-ID") Long memberId,
            @PathVariable Long categoryId,
            @RequestParam CouponType couponType) {
        List<MemberCouponDto> result = memberCouponService.getAvailableCouponsForCategory(memberId, categoryId, couponType);
        return ResponseEntity.ok(result);
    }



}
