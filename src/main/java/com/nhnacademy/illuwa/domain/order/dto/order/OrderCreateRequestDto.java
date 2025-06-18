package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private LocalDateTime requestedDeliveryDate;

}

// 프론트 -> order 서버 (주문 요청)