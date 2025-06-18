package com.nhnacademy.illuwa.domain.order.dto.order;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Builder
public class OrderCreateResponseDto {

    private Long orderId;
    private String orderNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deliveryDate;

    private BigDecimal finalPrice;
    private boolean isGuest;

    private BigDecimal discountPrice;

    @QueryProjection
    public OrderCreateResponseDto(Long orderId, String orderNumber, LocalDateTime orderDate, LocalDateTime deliveryDate, BigDecimal finalPrice, boolean isGuest, BigDecimal discountPrice) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.finalPrice = finalPrice;
        this.isGuest = isGuest;
        this.discountPrice = discountPrice;
    }

    public static OrderCreateResponseDto orderCreateResponseDto(Order order) {
        return OrderCreateResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getDeliveryDate())
                .finalPrice(order.getFinalPrice())
                .isGuest(Objects.isNull(order.getMemberId()))
                .discountPrice(order.getDiscountPrice())
                .build();
    }

}
