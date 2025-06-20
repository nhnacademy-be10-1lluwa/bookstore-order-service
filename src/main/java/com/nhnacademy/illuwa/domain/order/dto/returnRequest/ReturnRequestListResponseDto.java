package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class ReturnRequestListResponseDto {

    private long returnId;
    private LocalDateTime requestedAt;
    private LocalDateTime returnedAt;
    private ReturnReason reason;
    private ReturnStatus status;
    private long orderId;

    @QueryProjection
    public ReturnRequestListResponseDto(long returnId, LocalDateTime requestedAt, LocalDateTime returnedAt, ReturnReason reason, ReturnStatus status, long orderId) {
        this.returnId = returnId;
        this.requestedAt = requestedAt;
        this.returnedAt = returnedAt;
        this.reason = reason;
        this.status = status;
        this.orderId = orderId;
    }
}

// order 서버 -> 프론트 (주문 반품 조회 응답)