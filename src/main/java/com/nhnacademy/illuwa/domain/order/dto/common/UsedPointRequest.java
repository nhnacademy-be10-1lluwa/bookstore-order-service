package com.nhnacademy.illuwa.domain.order.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class UsedPointRequest {
    long memberId;
    BigDecimal usedPoint;
}
