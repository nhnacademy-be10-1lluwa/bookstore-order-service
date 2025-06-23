package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.returnRequest.AdminReturnRequestRegisterDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
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
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;

import static java.util.Objects.requireNonNull;

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

    // 차감 배송비
    // 출고일로 부터 10일 이내 미사용 시 반품 택배비 차감 후 가능
    @Override
    public ReturnRequest addReturnRequest(String orderId, ReturnRequestCreateRequestDto returnRequestCreateDto) {

        Order order = orderRepository.findByOrderId(parseId(orderId)).orElseThrow(() -> new NotFoundException("주문 내역을 찾을 수 없습니다.", parseId(orderId)));

        LocalDateTime shippedAt = order.getDeliveryDate();
        requireNonNull(shippedAt, "출고일 정보가 없습니다.");

        long days = ChronoUnit.DAYS.between(shippedAt, LocalDateTime.now());
        boolean isDamage = EnumSet.of(ReturnReason.Item_Damaged, ReturnReason.Defective_Item).contains(returnRequestCreateDto.getReason());

        int limit = isDamage ? 30 : 10;
        if (days > limit) {
            throw new BadRequestException("출고일로부터 " + limit + "일이 지나 반품이 불가합니다.");
        }

        BigDecimal feeDeducted = isDamage ? BigDecimal.ZERO : order.getShippingFee();

        if (repository.existsByOrder((order))) {
            throw new BadRequestException("이미 반품 요청된 주문입니다.");
        }

        order.setOrderStatus(OrderStatus.Returned);

        ReturnRequest returnRequest = ReturnRequest.builder()
                .order(order)
                .memberId(returnRequestCreateDto.getMemberId())
                .returnReason(returnRequestCreateDto.getReason())
                .shippingFeeDeducted(feeDeducted)
                .requestedAt(LocalDateTime.now())
                .status(ReturnStatus.Requested)
                .build();

        return repository.save(returnRequest);
    }

    @Override
    public void removeReturnRequest(String returnRequestId) {
        long id = parseId(returnRequestId);

        repository.findByReturnId(id).orElseThrow(()
                -> new NotFoundException("해당 반품 요청 내역을 찾을 수 없습니다.", id));

        repository.removeReturnRequestByReturnId(id);
    }

    @Override
    public void updateReturnRequest(Long returnId, AdminReturnRequestRegisterDto returnRequestRegisterDto) {

        repository.updateStatusByReturnRequestId(returnId, returnRequestRegisterDto);


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
