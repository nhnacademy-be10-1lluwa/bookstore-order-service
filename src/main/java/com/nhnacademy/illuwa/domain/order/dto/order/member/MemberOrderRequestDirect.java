package com.nhnacademy.illuwa.domain.order.dto.order.member;

import com.nhnacademy.illuwa.common.external.product.dto.OrderItemDto;
import com.nhnacademy.illuwa.domain.order.dto.order.common.BaseOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class MemberOrderRequestDirect extends BaseOrderRequest {

    private OrderItemDto item;
    private BigDecimal usedPoint;
    private Long memberCouponId;
}
