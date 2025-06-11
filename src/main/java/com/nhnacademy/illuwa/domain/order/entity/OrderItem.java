package com.nhnacademy.illuwa.domain.order.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private BigDecimal price; // 항목별 구매가격 = 도서가격 * 수량

    @ManyToOne
    private Packaging packaging;

    public OrderItem(long bookId, Order order, int quantity, BigDecimal price, Packaging packaging) {
        this.bookId = bookId;
        this.order = order;
        this.quantity = quantity;
        this.price = price;
        this.packaging = packaging;
    }
}
