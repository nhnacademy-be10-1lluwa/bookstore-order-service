package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestCreateRequestDto {

    private ZonedDateTime requestedAt;
    private String reason;
}

// 프론트 -> order 서버 (반품 요청을 요청)

// member Id, order Id
/*
*
*  /members/{memberId}/orders/{ordersId}/returns = 반품 post 요청
*
*
* */