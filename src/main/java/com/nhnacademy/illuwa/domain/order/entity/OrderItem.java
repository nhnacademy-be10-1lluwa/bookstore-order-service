package com.nhnacademy.illuwa.domain.order.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Table(
        name = "order_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_order_book",
                columnNames = {"order_id", "book_id"}
        )
)
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderItemId;

    private long bookId;

    @ManyToOne
    private Order order;

    @Setter
    private int quantity;

    @Setter
    private BigDecimal price;

    private long memberCouponId;

    private BigDecimal discountPrice;

    private BigDecimal itemTotalPrice;

    @ManyToOne
    private Packaging packaging;

    // 스냅샷용 포장 옵션
    private BigDecimal packagingPrice;

    @Builder
    public OrderItem(long bookId, Order order, int quantity, BigDecimal price, long memberCouponId, BigDecimal discountPrice, BigDecimal itemTotalPrice, Packaging packaging, BigDecimal packagingPrice) {
        this.bookId = bookId;
        this.order = order;
        this.quantity = quantity;
        this.price = price;
        this.memberCouponId = memberCouponId;
        this.discountPrice = discountPrice;
        this.itemTotalPrice = itemTotalPrice;
        this.packaging = packaging;
        this.packagingPrice = packagingPrice;
    }
}
