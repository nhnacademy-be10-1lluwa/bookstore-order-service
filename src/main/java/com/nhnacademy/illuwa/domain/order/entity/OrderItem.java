package com.nhnacademy.illuwa.domain.order.entity;


import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "order_item")
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderItemId;

    private long bookId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Setter
    private int quantity;

    @Setter
    private BigDecimal price;

    @Column(name = "member_coupon_id", nullable = true)
    private long memberCouponId; // erd X

    private BigDecimal discountPrice;

    private BigDecimal itemTotalPrice;

    @ManyToOne
    @JoinColumn(name = "packaging_id")
    private Packaging packaging;


    @Builder
    public OrderItem(long bookId, Order order, int quantity, BigDecimal price, long memberCouponId, BigDecimal discountPrice, BigDecimal itemTotalPrice, Packaging packaging) {
        this.bookId = bookId;
        this.order = order;
        this.quantity = quantity;
        this.price = price;
        this.memberCouponId = memberCouponId;
        this.discountPrice = discountPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.packaging = packaging;
    }
}
