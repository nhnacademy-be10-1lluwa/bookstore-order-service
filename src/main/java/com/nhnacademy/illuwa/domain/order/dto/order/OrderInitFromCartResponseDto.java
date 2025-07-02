package com.nhnacademy.illuwa.domain.order.dto.order;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInitFromCartResponseDto {
    private List<CartOrderItemDto> cartItems;
    private List<MemberAddressDto> recipients;
    private List<MemberCouponResponse> availableCoupons;
    private BigDecimal pointBalance;
}
