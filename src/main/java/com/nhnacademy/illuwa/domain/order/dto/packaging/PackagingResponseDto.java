package com.nhnacademy.illuwa.domain.order.dto.packaging;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackagingResponseDto {
    private long packagingId;
    private String packagingName;
    private BigDecimal packagingPrice;
}

// order 서버 -> 프론트 (포장지 조회 정보 전송)
