package com.nhnacademy.illuwa.domain.order.entity;

import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.Currency;

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

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @Column(name = "member_id")
    private long memberId;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "shipping_fee", nullable = false)
    private BigDecimal shippingFee;

    @ManyToOne
    @JoinColumn(name = "shipping_policy_id", referencedColumnName = "shipping_policy_id")
    private ShippingPolicy shippingPolicy;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "delivery_date", nullable = false)
    private LocalDateTime deliveryDate;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "discount_price")
    private BigDecimal discountPrice;

    @Column(name = "used_point")
    private BigDecimal usedPoint;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    // enum 주문 상태
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_contact", nullable = false)
    private String recipientContact;

    @Column(name = "read_address", nullable = false)
    private String readAddress;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "member_coupon_id", nullable = false)
    private Long memberCouponId;

    @Builder
    public Order(long orderId, String orderNumber, long memberId, Long guestId, BigDecimal shippingFee, ShippingPolicy shippingPolicy, LocalDateTime orderDate, LocalDateTime deliveryDate, BigDecimal totalPrice, BigDecimal discountPrice, BigDecimal usedPoint, BigDecimal finalPrice, OrderStatus orderStatus, String recipientName, String recipientContact, String readAddress, String detailAddress, Long memberCouponId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.memberId = memberId;
        this.guestId = guestId;
        this.shippingFee = shippingFee;
        this.shippingPolicy = shippingPolicy;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalPrice = totalPrice;
        this.discountPrice = discountPrice;
        this.usedPoint = usedPoint;
        this.finalPrice = finalPrice;
        this.orderStatus = orderStatus;
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.readAddress = readAddress;
        this.detailAddress = detailAddress;
        this.memberCouponId = memberCouponId;
    }
}
