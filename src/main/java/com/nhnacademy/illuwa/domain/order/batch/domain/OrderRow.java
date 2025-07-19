package com.nhnacademy.illuwa.domain.order.batch.domain;

import java.math.BigDecimal;

public record OrderRow(Long orderId, Long memberId, BigDecimal totalPrice) {
}
