package com.nhnacademy.illuwa.domain.order.dto.packaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackagingUpdateRequestDto {

    private String packagingName;
    private BigDecimal packagingPrice;
}

// 프론트 -> order 서버 (패키지 수정 요청)
