package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;

import java.util.List;

public interface PackagingService {

    // 포장 옵션 전체 조회 - 사용하지 않을 듯(?)
    List<PackagingResponseDto> getAllPackaging();

    // 활성된 포장 옵션 조회
    List<PackagingResponseDto> getPackagingByActive();

    // 단일 포장 옵션 조회
    PackagingResponseDto getPackaging(String packagingId);

    // 포장 옵션 추가
    Packaging addPackaging(PackagingCreateRequestDto packagingCreateDto);

    // 포장 옵션 삭제 (활성화 여부 false 로 변경)
    int removePackaging(String packagingId);

    // 포장 옵션 수정
    Packaging updatePackaging(String packagingId, PackagingCreateRequestDto packagingCreateDto);


}
