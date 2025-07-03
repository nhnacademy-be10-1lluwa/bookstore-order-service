package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemRequestDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {

    private Long memberId;

    private Long guestId;

    @NotNull
    private Long shippingPolicyId;

    @Size(min=1)
    @NotNull
    private List<OrderItemRequestDto> items;

    private LocalDate requestedDeliveryDate;

}

// 프론트 -> order 서버 (주문 요청)