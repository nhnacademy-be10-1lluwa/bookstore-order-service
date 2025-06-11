package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import com.nhnacademy.illuwa.domain.order.ReturnReason;
import com.nhnacademy.illuwa.domain.order.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestListResponseDto {

    private long returnId;
    private ZonedDateTime requestedAt;
    private ZonedDateTime returnedAt;
    private ReturnReason reason;
    private ReturnStatus status;
    private long orderId;
}

// order 서버 -> 프론트 (주문 반품 조회 응답)