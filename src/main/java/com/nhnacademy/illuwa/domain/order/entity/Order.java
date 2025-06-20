package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long orderId;


    // 양방향 매핑 : order 삽입 -> orderItem 삽입
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private final List<OrderItem> items = new ArrayList<>();

    private String orderNumber;

    private long memberId;

    @ManyToOne
    @JoinColumn(name = "shipping_policy_id", referencedColumnName = "shipping_policy_id")
    private ShippingPolicy shippingPolicy;


    private LocalDateTime orderDate;


    private LocalDateTime deliveryDate;


    private BigDecimal totalPrice;


    private BigDecimal discountPrice;


    private BigDecimal usedPoint;


    private BigDecimal finalPrice;

    // enum 주문 상태
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Builder
    public Order(long memberId, String orderNumber, ShippingPolicy shippingPolicy, LocalDateTime orderDate, LocalDateTime deliveryDate, BigDecimal totalPrice, BigDecimal discountPrice, BigDecimal usedPoint, BigDecimal finalPrice, OrderStatus orderStatus) {
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
