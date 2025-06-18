package com.nhnacademy.illuwa.domain.order.dto.orderItem;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemRequestDto {

    @NotNull
    private Long bookId;

    @Positive
    private Integer quantity;

    private Long memberCouponId;

    @NotNull
    private Long packagingId;
}
