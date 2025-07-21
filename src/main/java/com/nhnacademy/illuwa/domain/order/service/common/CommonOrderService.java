package com.nhnacademy.illuwa.domain.order.service.common;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;

public interface CommonOrderService {

    // id로 주문 상태 변경하기 (COMMON)
    void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto);

    // 결제 완료 (COMMON)
    void updateOrderPaymentByOrderNumber(String orderNumber);

    // 주문 취소하기 (COMMON)
    void orderCancel(Long orderId);

    // orderNumber로 결제 취소하기 (COMMON)
    OrderResponseDto cancelOrderByOrderNumber(String orderNumber);

    // id로 주문 환불하기 (COMMON)
    OrderResponseDto refundOrderById(Long orderId, ReturnRequestCreateRequestDto dto);
}
