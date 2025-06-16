package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long orderId;

    private String orderNumber;

    private long memberId;

    @ManyToOne
    @JoinColumn(name = "shipping_policy_id", referencedColumnName = "shipping_policy_id")
    private ShippingPolicy shippingPolicy;


    private ZonedDateTime orderDate;


    private ZonedDateTime deliveryDate;


    private BigDecimal totalPrice;


    private BigDecimal discountPrice;


    private BigDecimal usedPoint;


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
