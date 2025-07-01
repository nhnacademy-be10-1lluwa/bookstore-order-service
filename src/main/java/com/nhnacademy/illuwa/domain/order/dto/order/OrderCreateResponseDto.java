package com.nhnacademy.illuwa.domain.order.dto.order;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateResponseDto {

    private Long orderId;
    private String orderNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deliveryDate;

    private BigDecimal finalPrice;

    private BigDecimal discountPrice;

    private OrderStatus orderStatus;

    public static OrderCreateResponseDto fromEntity(Order order) {
        return OrderCreateResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .finalPrice(order.getFinalPrice())
                .discountPrice(order.getDiscountPrice())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
