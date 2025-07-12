package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class OrderListResponseDto {

    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private LocalDate deliveryDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;

    @QueryProjection
    public OrderListResponseDto(Long orderId, String orderNumber, LocalDateTime orderDate, LocalDate deliveryDate, BigDecimal totalPrice, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public static OrderListResponseDto orderListResponseDto(Order order) {
        return OrderListResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}

// order 서버 -> 프론트 (Admin/ 주문 목록 응답(리스트용) )

// 주문 list 반환용