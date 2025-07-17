package com.nhnacademy.illuwa.domain.coupons.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String errorType; // birthday_coupon

    private String errorMessage; // 예외 메시지

    private String stackTrace; // 전체 스택트레이스 저장

    private LocalDateTime occurredAt; // 장애 발생 시각
}
