package com.nhnacademy.illuwa.domain.order.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointSavedEvent {
    private Long memberId;
    private BigDecimal price;
}

