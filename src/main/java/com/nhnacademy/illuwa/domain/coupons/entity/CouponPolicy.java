package com.nhnacademy.illuwa.domain.coupons.entity;

import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 30)
    private String code; // 고유 id 식별 x -> 코드로 식별o

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CouponStatus status; // 상태

    private int minOrderAmount; // 최소 주문 금액
    private Integer discountAmount; // 할인 금액
    private Integer discountPercent; // 할인 퍼센트
    private Integer maxDiscountAmount; // 최대 할인 금액

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @PrePersist // DB 저장하기전 자동 호출
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
        if (Objects.isNull(this.status)) {
            this.status = CouponStatus.ACTIVE; // 기본상태 활성
        }
    }

    @PreUpdate // DB 반영하기 직전에 자동 호출
    public void preUpdate() {
        this.updateAt = LocalDateTime.now();
    }

}
