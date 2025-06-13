package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    private String orderNumber;

    private long memberId;

    @ManyToOne
    private ShippingPolicy shippingPolicy;


    @Setter
    private ZonedDateTime orderDate;

    @Setter
    private ZonedDateTime deliveryDate;

    @Setter
    private BigDecimal totalPrice;

    @Setter
    private BigDecimal discountPrice;

    @Setter
    private BigDecimal usedPoint;

    @Setter
    private BigDecimal finalPrice;

    // enum 주문 상태
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder
    public Order(long memberId, String orderNumber, ShippingPolicy shippingPolicy, ZonedDateTime orderDate, ZonedDateTime deliveryDate, BigDecimal totalPrice, BigDecimal discountPrice, BigDecimal usedPoint, BigDecimal finalPrice, OrderStatus orderStatus) {
        this.memberId = memberId;
        this.orderNumber = orderNumber;
        this.shippingPolicy = shippingPolicy;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.usedPoint = usedPoint;
        this.finalPrice = finalPrice;
        this.orderStatus = orderStatus;
    }
}
