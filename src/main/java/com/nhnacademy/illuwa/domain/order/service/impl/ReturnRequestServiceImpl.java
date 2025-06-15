package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.ReturnRequestRepository;
import com.nhnacademy.illuwa.domain.order.service.ReturnRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository repository;

    public ReturnRequestServiceImpl(ReturnRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestListResponseDto> getAllReturnRequest() {
        return repository.findAll().stream().map(this::toListResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestListResponseDto> findPendingReturnRequests() {
        return repository.findByReturnedAtIsNull().stream().map(this::toListResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnRequestResponseDto getReturnRequest(String returnRequestId) {
        long id = parseId(returnRequestId);
        ReturnRequest returnRequest = repository.findByReturnId(id).orElseThrow(()
                -> new NotFoundException("해당 반품 내역을 찾을 수 없습니다.", id));
        return toResponseDto(returnRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestListResponseDto> findReturnRequestsByMemberId(String memberId) {
        return repository.findByMemberId(memberId).stream().map(this::toListResponseDto).toList();
    }

    // fixme order service 만들고 확인하기
    /*@Override
    public ReturnRequest addReturnRequest(String memberId, String orderId, ReturnRequestCreateRequestDto returnRequestCreateDto) {
        ReturnRequest rr = ReturnRequest.builder()
                .memberId(memberId)
                .order()
                .requestedAt(ZonedDateTime.now())
                .returnedAt(null)
                .shippingFeeDeducted(returnRequestCreateDto)
        return null;
    }*/

    @Override
    public int removeReturnRequest(String returnRequestId) {
        long id = parseId(returnRequestId);
        return repository.removeReturnRequestByReturnId(id);
    }

    // Entity -> Dto 리스트용 dto
    private ReturnRequestListResponseDto toListResponseDto(ReturnRequest rr) {
        return new ReturnRequestListResponseDto(rr.getReturnId(),
                rr.getRequestedAt(),
                rr.getReturnedAt(),
                rr.getReturnReason(),
                rr.getStatus(),
                rr.getOrder().getOrderId());
    }

    // Entity -> Dto 상세정보용 dto
    private ReturnRequestResponseDto toResponseDto(ReturnRequest rr) {
        return new ReturnRequestResponseDto(rr.getMemberId(),
                rr.getRequestedAt(),
                rr.getReturnedAt(),
                rr.getShippingFeeDeducted(),
                rr.getReturnReason(),
                rr.getStatus(),
                rr.getOrder().getOrderId());
    }

    // ID 파싱 오류(잘못된 숫자 포맷)
    private long parseId(String returnRequestId) {
        try {
            return Long.parseLong(returnRequestId);
        } catch (NumberFormatException e) {
            throw new BadRequestException("유효하지 않은 반품 ID: " + returnRequestId);
        }
    }
}
