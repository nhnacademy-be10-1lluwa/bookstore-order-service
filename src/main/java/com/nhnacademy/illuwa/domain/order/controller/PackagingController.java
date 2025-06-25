package com.nhnacademy.illuwa.domain.order.controller;


import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/packaging")
public class PackagingController {

    private final PackagingService packagingService;

    // 포장 옵션 생성
    @PostMapping
    public ResponseEntity<PackagingResponseDto> createPackaging(@RequestBody @Valid PackagingCreateRequestDto request) {
        PackagingResponseDto response = packagingService.addPackaging(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 포장 옵션 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<PackagingResponseDto> getPackaging(@PathVariable Long id) {
        PackagingResponseDto response = packagingService.getPackaging(id);
        return ResponseEntity.ok(response);
    }

    // 포장 옵션 전체 조회 (활성화)
    @GetMapping
    public ResponseEntity<List<PackagingResponseDto>> getAllPackaging() {
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        return ResponseEntity.ok(packaging);
    }

    // 포장 옵션 수정 (기존 포장 옵션 비활성화 후, 새로운 포장 옵션 생성)
    @PutMapping("/{id}")
    public ResponseEntity<PackagingResponseDto> updatePackaging(
            @PathVariable Long id,
            @RequestBody PackagingCreateRequestDto request) {
        PackagingResponseDto response = packagingService.updatePackaging(id, request);

        return ResponseEntity.ok(response);
    }

    // 포장 옵션 삭제 (비활성화)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackaging(@PathVariable Long id) {
        packagingService.removePackaging(id);
        return ResponseEntity.noContent().build();
    }

}
