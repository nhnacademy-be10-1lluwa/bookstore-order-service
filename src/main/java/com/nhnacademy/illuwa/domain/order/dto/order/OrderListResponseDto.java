package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class OrderListResponseDto {

    private long orderId;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;

    @QueryProjection
    public OrderListResponseDto(long orderId, LocalDateTime orderDate, BigDecimal totalPrice, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public static OrderListResponseDto orderListResponseDto(Order order) {
        return OrderListResponseDto.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}

// order 서버 -> 프론트 (Admin/ 주문 목록 응답(리스트용) )

// 주문 list 반환용