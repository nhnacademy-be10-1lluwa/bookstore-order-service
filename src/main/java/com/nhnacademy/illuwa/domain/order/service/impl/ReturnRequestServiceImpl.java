package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.ReturnRequestRepository;
import com.nhnacademy.illuwa.domain.order.service.ReturnRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReturnRequestServiceImpl implements ReturnRequestService {

    @Autowired
    private ReturnRequestRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestListResponseDto> getAllReturnRequest() {
        return repository.findAllDtos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestListResponseDto> findPendingReturnRequests() {
        return repository.findReturnRequestDtosByIsNull();
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnRequestResponseDto getReturnRequest(String returnRequestId) {
        long id = parseId(returnRequestId);
        return repository.findByReturnRequestId(id).orElseThrow(()
                -> new NotFoundException("해당 반품 내역을 찾을 수 없습니다.", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnRequestListResponseDto> findReturnRequestsByMemberId(String memberId) {
        long id = parseId(memberId);
        return repository.findByMemberId(id);
    }

    // fixme order service 만들고 확인하기
    @Override
    public ReturnRequest addReturnRequest(String orderId, ReturnRequestCreateRequestDto returnRequestCreateDto) {
        long id = parseId(orderId);
        Order order = orderRepository.findByOrderId(id).orElseThrow(() ->
                new NotFoundException("해당 주문을 찾을 수 없습니다.", id));

        BigDecimal feeDeducted = new BigDecimal("3000");

        return ReturnRequest.builder()
                .memberId(returnRequestCreateDto.getMemberId())
                .requestedAt(LocalDateTime.now())
                .returnedAt(null)
                .shippingFeeDeducted(feeDeducted) // todo 배송료 만들기
                .returnReason(returnRequestCreateDto.getReason())
                .status(ReturnStatus.Requested)
                .order(order).build();
    }

    @Override
    public void removeReturnRequest(String returnRequestId) {
        long id = parseId(returnRequestId);

        repository.findByReturnId(id).orElseThrow(()
                -> new NotFoundException("해당 반품 요청 내역을 찾을 수 없습니다.", id));

        repository.removeReturnRequestByReturnId(id);
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
