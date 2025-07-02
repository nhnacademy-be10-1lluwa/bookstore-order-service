package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderInitResponseDto {
    private List<CartOrderItemDto> cartItems;
    private List<PackagingResponseDto> packaging;
}
