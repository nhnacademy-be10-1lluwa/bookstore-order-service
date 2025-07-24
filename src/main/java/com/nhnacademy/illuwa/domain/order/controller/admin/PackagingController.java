package com.nhnacademy.illuwa.domain.order.controller.admin;


import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
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

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order/packagings")
@Tag(name = "포장 옵션 API", description = "포장 옵션 CRUD API")
public class PackagingController {

    private final PackagingService packagingService;

    // 포장 옵션 전체 조회 (활성화)
    @Operation(summary = "포장 옵션 조회", description = "활성화 된 포장 옵션을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<PackagingResponseDto>> getAllPackaging() {
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        return ResponseEntity.ok(packaging);
    }

    // 포장 옵션 단일 조회
    @Operation(summary = "포장 옵션 상세 조회", description = "활성화 된 포장 옵션을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @GetMapping("/{packaging-id}")
    public ResponseEntity<PackagingResponseDto> getPackaging(
            @Parameter(description = "포장옵션 ID", required = true)
            @PathVariable("packaging-id") Long id) {
        PackagingResponseDto response = packagingService.getPackaging(id);
        return ResponseEntity.ok(response);
    }

    // 포장 옵션 생성
    @Operation(summary = "포장 옵션 추가", description = "어드민이 포장 옵션을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<PackagingResponseDto> createPackaging(@RequestBody @Valid PackagingCreateRequestDto request) {
        PackagingResponseDto response = packagingService.addPackaging(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
    아래는 미사용 컨트롤러
    ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    * */

    // 포장 옵션 수정 (기존 포장 옵션 비활성화 후, 새로운 포장 옵션 생성)
    @Operation(hidden = true)
    @PutMapping("/{id}")
    public ResponseEntity<PackagingResponseDto> updatePackaging(
            @PathVariable Long id,
            @RequestBody PackagingCreateRequestDto request) {
        PackagingResponseDto response = packagingService.updatePackaging(id, request);

        return ResponseEntity.ok(response);
    }

    // 포장 옵션 삭제 (비활성화)
    @Operation(summary = "포장 옵션 삭제", description = "어드민이 포장 옵션을 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackaging(
            @Parameter(name = "id", description = "포장 옵션 ID", required = true)
            @PathVariable Long id) {
        packagingService.removePackaging(id);
        return ResponseEntity.noContent().build();
    }

}
