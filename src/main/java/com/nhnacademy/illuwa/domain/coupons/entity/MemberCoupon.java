package com.nhnacademy.illuwa.domain.coupons.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static java.math.BigDecimal.ONE;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 현재 회원 쿠폰이라는 엔티티는
// 1. 회원이라는 테이블이 존재해야함
// 2. 쿠폰 정책에 의해 파생된 쿠폰이 존재해야함.
// 3. 위 두가지중 하나라도 없을시 해당 엔티티는 생성이 불가능함
// 이 상황의 시나리오는 ? -> 백엔드 쪽에서는 memberCouponRepo.getId를 활용해서 회원이 존재하지 않을 시 에러 발생
// 프론트 쪽에서는 ? -> 응답본문에 404에러가 포함되어있을시 새 창이나 해당 페이지에서 에러메시지 or 에러페이지 반환
public class MemberCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쿠폰을 가진 사용자
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 어떠한 쿠폰을 가지고 있는지 판별
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private boolean used; // 사용여부
    private LocalDate usedAt; // 사용 일자
    private LocalDate issuedAt; // 발급 일자
    private LocalDate expiresAt; // 실제 유효기간

    // 발급과 동시에 유효기간 설정
    @PrePersist
    public void prePersist() {
        if (Objects.nonNull(issuedAt)) {
            this.expiresAt = this.issuedAt.plusDays(30);
        }
    }

//     사용여부가 true 일 경우 현재 날짜로 사용일자 업데이트
    @PreUpdate
    public void preUpdate() {
        if (!used) {
            this.usedAt = LocalDate.now();
        }
    }
}
