package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.returnRequest.AdminReturnRequestRegisterDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;

import java.util.List;

public interface ReturnRequestService {

    // 반품 전체 조회 (ADMIN)
    List<ReturnRequestListResponseDto> getAllReturnRequest();

    // 처리 대기 중인(미확인) 반품 요청 전체 조회
    List<ReturnRequestListResponseDto> findPendingReturnRequests();

    // 반품 ID로 반품 내역 조회(ADMIN, MEMBERS))
    ReturnRequestResponseDto getReturnRequest(String returnRequestId);

    // 특정 회원의 모든 반품 요청 조회(ADMIN, MEMBERS)
    List<ReturnRequestListResponseDto> findReturnRequestsByMemberId(String memberId);

    // 반품 요청하기 (MEMBERS)
//    ReturnRequest addReturnRequest(String orderId, ReturnRequestCreateRequestDto returnRequestCreateDto);

    // 반품 취소하기 (MEMBERS)
    void removeReturnRequest(String returnRequestId);

    // 반품 수락하기 (ADMIN)
    void updateReturnRequest(Long returnId, AdminReturnRequestRegisterDto returnRequestRegisterDto);

}
