package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderCreateRequestDto {

    private Long memberId;

    @NotNull
    private Long shippingPolicyId;

    @Size(min=1)
    @NotNull
    private List<OrderItem> items;

    private ZonedDateTime requestedDeliveryDate;

}

// 프론트 -> order 서버 (주문 요청)