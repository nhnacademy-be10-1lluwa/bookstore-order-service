package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    // (관리자용 -> 정책기반) 쿠폰 단건 조회 {id}
    // 프론트쪽에서는 쿠폰의 목록을 보고 관리자가 기존에 쿠폰 정보를 보고 수정할수 있겠지만
    // 백엔드 입장에서는 기존의 쿠폰 정보를 보려면 /coupons/{@@@}이라는 url 요청을 한 번 해야지 확인이 가능
    // 백엔드 관리자 입장에서도 편의성을 위해 따로 추가
    // ID로 한 이유 -> 클라이언트 입장에서는 굳이 생성된 고유 ID 필드를 몰라도 되기때문에

    @Operation(
            summary = "정책기반 쿠폰 단건 조회",
            description = "쿠폰의 내부 식별자(ID)로 단건을 조회합니다.",
            tags = "관리자 쿠폰"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰입니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long id) {
        CouponResponse response = couponService.getCouponById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "정책기반 쿠폰 생성",
            description = "쿠폰 정책(할인율)을 기준으로 새로운 쿠폰을 생성합니다.",
            tags = "관리자 쿠폰"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "쿠폰 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효 시작일은 유효 종료일 이전이어야 합니다."),
            @ApiResponse(responseCode = "404", description = "해당 정책코드는 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 쿠폰 입니다."),
            @ApiResponse(responseCode = "409", description = "쿠폰 정책 상태가 비활성화이므로 생성이 불가합니다."),
    })
    // (관리자용 -> 정책기반) 쿠폰 생성
    @PostMapping
    public ResponseEntity<CouponCreateResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request) {
        CouponCreateResponse response = couponService.createCoupon(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "정책기반 쿠폰 수정",
            description = "쿠폰(ID)로 기존 쿠폰 정보를 수정합니다. (RequestBody로 업데이트 정보 전달)",
            tags = "관리자 쿠폰"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "유효 시작일은 유효 종료일 이전이어야 합니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰ID 입니다."),
    })
    // (관리자용 -> 정책기반) 쿠폰 수정
    @PutMapping("{id}")
    public ResponseEntity<CouponUpdateResponse> updateCoupon(
            @PathVariable Long id,
            @RequestBody @Valid CouponUpdateRequest request) {
        CouponUpdateResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "정책기반 쿠폰 삭제",
            description = "쿠폰(ID)로 기존 쿠폰를 삭제합니다.",
            tags = "관리자 쿠폰"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰ID 입니다."),
    })
    // (관리자용 -> 정책기반) 쿠폰 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
