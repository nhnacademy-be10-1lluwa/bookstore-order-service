package com.nhnacademy.illuwa.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.BindParam;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쿠폰을 가진 사용자
//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    private Member member;

    // 어떠한 쿠폰을 가지고 있는지 판별
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private boolean used; // 사용여부
    private LocalDate issuedAt; // 발급 일자
    private LocalDate expiresAt; // 실제 유효기간
}
