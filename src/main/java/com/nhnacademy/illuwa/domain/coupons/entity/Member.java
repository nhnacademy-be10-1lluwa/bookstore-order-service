package com.nhnacademy.illuwa.domain.coupons.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 회원 임시테이블
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate birth;

    @Email
    private String email;

    private String name;
}
