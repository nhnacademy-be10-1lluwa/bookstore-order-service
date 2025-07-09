package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestCreateRequestDto {

    private LocalDateTime requestedAt;
    private ReturnReason reason;
}

// 프론트 -> order 서버 (반품 요청을 요청)

// member Id, order Id
/*
*
*  /members/{memberId}/orders/{ordersId}/returns = 반품 post 요청
*
*
* */