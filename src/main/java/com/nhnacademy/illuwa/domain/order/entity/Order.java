package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;

    private long memberId;

    @ManyToOne
    private ShippingPolicy shippingPolicy;

    @Setter
    private ZonedDateTime orderDate;

    @Setter
    private ZonedDateTime deliveryDate;

    @Setter
    private BigDecimal totalPrice;

    // enum 주문 상태
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Setter
    private String orderPassword;

    public Order(long memberId, ShippingPolicy shippingPolicy, ZonedDateTime orderDate, ZonedDateTime deliveryDate, BigDecimal totalPrice, OrderStatus orderStatus) {
        this.memberId = memberId;
        this.shippingPolicy = shippingPolicy;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
    }

    // 비회원 생성자
    public Order(long memberId, ShippingPolicy shippingPolicy, ZonedDateTime orderDate, ZonedDateTime deliveryDate, BigDecimal totalPrice, OrderStatus orderStatus, String orderPassword) {
        this.memberId = memberId;
        this.shippingPolicy = shippingPolicy;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderPassword = orderPassword;
    }
}
