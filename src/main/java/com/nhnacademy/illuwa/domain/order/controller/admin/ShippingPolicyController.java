package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.service.ShippingPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/order/shipping-policy")
@Tag(name = "배송비 정책 API", description = "배송정책 관련 CRUD API")
public class ShippingPolicyController {

    private final ShippingPolicyService shippingPolicyService;

    // 배송비 정책 생성
    @Operation(summary = "배송비 정책 생성", description = "어드민이 배송 정책을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<ShippingPolicyResponseDto> createShippingPolicy(@RequestBody @Valid ShippingPolicyCreateRequestDto request) {
        ShippingPolicyResponseDto response =  shippingPolicyService.addShippingPolicy(request);
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 배송비 정책 단일 조회
    @Operation(summary = "배송 정책 상세 조회", description = "배송 정책을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ShippingPolicyResponseDto> getShippingPolicy(
            @Parameter(name = "id", description = "배송 정책 ID", required = true)
            @PathVariable Long id) {
        ShippingPolicyResponseDto response = shippingPolicyService.getShippingPolicy(id);
        return ResponseEntity.ok(response);
    }

    // 배송비 정책 조회 (활성화 된 배송비 정책)
    @Operation(summary = "배송 정책 전체 조회", description = "활성화된 배송 정책을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @GetMapping
    public ResponseEntity<ShippingPolicyResponseDto> getAllShippingPolicy() {
        ShippingPolicyResponseDto response = shippingPolicyService.getShippingPolicyByActive(true);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송 정책 수정", description = "어드민이 배송 정책을 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "배송 정책을 찾을 수 없습니다"),
        @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ShippingPolicyResponseDto> updateShippingPolicy(
            @Parameter(name = "id", description = "배송 정책 ID", required = true)
            @PathVariable Long id,
            @RequestBody ShippingPolicyCreateRequestDto request) {
        ShippingPolicyResponseDto response = shippingPolicyService.updateShippingPolicy(id, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송 정책 삭제", description = "배송 정책을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShippingPolicy(
            @Parameter(name = "id", description = "배송 정책 ID", required = true)
            @PathVariable Long id) {
        shippingPolicyService.removeShippingPolicy(id);
        return ResponseEntity.noContent().build();
    }


}
