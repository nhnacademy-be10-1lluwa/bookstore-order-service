package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private long orderId;
    private long memberId;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;

    public static OrderResponseDto orderResponseDto(Order order) {
         return OrderResponseDto.builder()
                 .orderId(order.getOrderId())
                 .memberId(order.getMemberId())
                 .orderDate(order.getOrderDate())
                 .deliveryDate(order.getDeliveryDate())
                 .totalPrice(order.getTotalPrice())
                 .orderStatus(order.getOrderStatus())
                 .build();
    }


}

// order 서버 -> 프론트 (주문 상세 조회 응답)
