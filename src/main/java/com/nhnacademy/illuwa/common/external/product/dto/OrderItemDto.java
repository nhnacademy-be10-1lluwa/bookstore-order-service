package com.nhnacademy.illuwa.common.external.product.dto;

import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long bookId;
    private Integer quantity;
    private Long packagingId;
    private Long couponId;

}
