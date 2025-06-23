package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.returnRequest.AdminReturnRequestRegisterDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestResponseDto;

import java.util.List;
import java.util.Optional;

public interface ReturnRequestQuerydslRepository {
    // 전체 조회
    List<ReturnRequestListResponseDto> findAllDtos();

    // 처리 대기 중인 반품 요청 전체 확인
    List<ReturnRequestListResponseDto> findReturnRequestDtosByIsNull();

    // 반품 ID로 단일 조회 (ADMIN)
    Optional<ReturnRequestResponseDto> findByReturnRequestId(Long returnRequestId);

    // 유저별 반품 내역 조회 (MEMBERS)
    List<ReturnRequestListResponseDto> findByMemberId(Long memberId);

    // 반품 상태 변경하기 (ADMIN)
    void updateStatusByReturnRequestId(Long returnRequestId, AdminReturnRequestRegisterDto adminReturnRequestRegisterDto);

}
