package com.nhnacademy.illuwa.domain.order.controller;

import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.service.ShippingPolicyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/shipping-policy")
public class ShippingPolicyController {

    private final ShippingPolicyService shippingPolicyService;

    // 배송비 정책 생성
    @PostMapping
    public ResponseEntity<ShippingPolicyResponseDto> createShippingPolicy(@RequestBody @Valid ShippingPolicyCreateRequestDto request) {
        ShippingPolicyResponseDto response =  shippingPolicyService.addShippingPolicy(request);
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 배송비 정책 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<ShippingPolicyResponseDto> getShippingPolicy(@PathVariable Long id) {
        ShippingPolicyResponseDto response = shippingPolicyService.getShippingPolicy(id);
        return ResponseEntity.ok(response);
    }

    // 배송비 정책 전체 조회 (활성화 된 배송비 정책)
    @GetMapping
    public ResponseEntity<List<ShippingPolicyResponseDto>> getAllShippingPolicy() {
        List<ShippingPolicyResponseDto> response = shippingPolicyService.getShippingPolicyByActive(true);

        return ResponseEntity.ok(response);
    }

    // 배송비 정책 수정
    @PutMapping("/{id}")
    public ResponseEntity<ShippingPolicyResponseDto> updateShippingPolicy(
            @PathVariable Long id,
            @RequestBody ShippingPolicyCreateRequestDto request) {
        ShippingPolicyResponseDto response = shippingPolicyService.updateShippingPolicy(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShippingPolicy(@PathVariable Long id) {
        shippingPolicyService.removeShippingPolicy(id);
        return ResponseEntity.noContent().build();
    }


}
