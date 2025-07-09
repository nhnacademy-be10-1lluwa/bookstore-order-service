package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {

    private Long orderId; // orderNumber
    private String orderNumber;
    private Long memberId; // 회원 넘버
    private LocalDateTime orderDate; //
    private LocalDate deliveryDate;
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private List<OrderItemResponseDto> items;

    @QueryProjection
    public OrderResponseDto(Long orderId, String orderNumber, Long memberId, LocalDateTime orderDate, LocalDate deliveryDate, BigDecimal shippingFee, BigDecimal totalPrice, OrderStatus orderStatus, List<OrderItemResponseDto> items) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.memberId = memberId;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.shippingFee = shippingFee;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.items = items;
    }



    public static OrderResponseDto orderResponseDto(Order order) {
         return OrderResponseDto.builder()
                 .orderId(order.getOrderId())
                 .orderNumber(order.getOrderNumber())
                 .memberId(order.getMemberId())
                 .orderDate(order.getOrderDate())
                 .deliveryDate(order.getDeliveryDate())
                 .shippingFee(order.getShippingFee())
                 .totalPrice(order.getTotalPrice())
                 .orderStatus(order.getOrderStatus())
                 .build();
    }


}

// order 서버 -> 프론트 (주문 상세 조회 응답)
