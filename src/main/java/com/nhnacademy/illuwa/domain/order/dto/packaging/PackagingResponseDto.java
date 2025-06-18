package com.nhnacademy.illuwa.domain.order.dto.packaging;

import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class PackagingResponseDto {
    private long packagingId;
    private String packagingName;
    private BigDecimal packagingPrice;

    @QueryProjection
    public PackagingResponseDto(long packagingId, String packagingName, BigDecimal packagingPrice) {
        this.packagingId = packagingId;
        this.packagingName = packagingName;
        this.packagingPrice = packagingPrice;
    }
}

// order 서버 -> 프론트 (포장지 조회 정보 전송)
