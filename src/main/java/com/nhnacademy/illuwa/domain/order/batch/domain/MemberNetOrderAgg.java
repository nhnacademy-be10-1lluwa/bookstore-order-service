package com.nhnacademy.illuwa.domain.order.batch.domain;

import java.math.BigDecimal;


// Reader가 반환할 DTO
public record MemberNetOrderAgg(Long memberId, BigDecimal totalPrice) {}
