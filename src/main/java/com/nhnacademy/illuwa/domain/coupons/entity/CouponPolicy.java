package com.nhnacademy.illuwa.domain.coupons.entity;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "coupon_policy")
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code; // 고유 id 식별 x -> 코드로 식별o

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private CouponStatus status; // 상태

    @Column(name = "min_order_amount",nullable = false)
    private BigDecimal minOrderAmount; // 최소 주문 금액

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 10)
    private DiscountType discountType; // 할인타입 선정

    // 이부분을 둘다 기본적으로 Null로 놔두는것보다
    // enum으로 (금액, 퍼센트)로 설정하여서 하는게 더 좋긴할듯
    @Column(name = "discount_amount")
    private BigDecimal discountAmount; // 할인 금액
    @Column(name = "discount_percent")
    private BigDecimal discountPercent; // 할인 퍼센트

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount; // 최대 할인 금액

    // 정책의 수정 여부를 확인 (솔직히 해당사항이 꼭 필요한건 아니지만 있으면 좋을것 같은 기능이라서 넣음)
    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @PrePersist // DB 저장하기전 자동 호출
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        if (Objects.isNull(this.status)) {
            this.status = CouponStatus.ACTIVE; // 기본상태 활성
        }
    }

    @PreUpdate // DB 반영하기 직전에 자동 호출
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

}
