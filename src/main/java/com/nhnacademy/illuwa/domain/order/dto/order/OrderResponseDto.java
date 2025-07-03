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
@Builder
@NoArgsConstructor
public class OrderResponseDto {

    private long orderId; // orderNumber
    private String orderNumber;
    private long memberId; // 회원 넘버
    private LocalDateTime orderDate; //
    private LocalDate deliveryDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;

    @QueryProjection
    public OrderResponseDto(long orderId, String orderNumber, long memberId, LocalDateTime orderDate, LocalDate deliveryDate, BigDecimal totalPrice, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    public static OrderResponseDto orderResponseDto(Order order) {
         return OrderResponseDto.builder()
                 .orderId(order.getOrderId())
                 .orderNumber(order.getOrderNumber())
                 .memberId(order.getMemberId())
                 .orderDate(order.getOrderDate())
                 .deliveryDate(order.getDeliveryDate())
                 .totalPrice(order.getTotalPrice())
                 .orderStatus(order.getOrderStatus())
                 .build();
    }


}

// order 서버 -> 프론트 (주문 상세 조회 응답)
