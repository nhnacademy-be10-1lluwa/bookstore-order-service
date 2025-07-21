package com.nhnacademy.illuwa.domain.order.service.admin;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminOrderService {

    // 전체 주문 내역 조회(ADMIN)
    Page<OrderListResponseDto> getAllOrders(Pageable pageable);

    // orderId로 주문 내역 조회(ADMIN)
    OrderResponseDto getOrderByOrderId(Long orderId);

    // 배달 날짜 변경
    void updateOrderDeliveryDate(Long orderId, LocalDate localDate);

    // number 로 주문 내역 조회(ADMIN)
    OrderResponseDto getOrderByNumber(String orderNumber);

    // member 별 주문 내역 조회 (ADMIN, MEMBERS)
    Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable);

    // 주문 상태별 조회 (ADMIN)
    Page<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status, Pageable pageable);

}
