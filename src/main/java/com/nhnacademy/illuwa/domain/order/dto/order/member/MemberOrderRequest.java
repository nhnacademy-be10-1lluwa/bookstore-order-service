package com.nhnacademy.illuwa.domain.order.dto.order.member;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.order.dto.order.common.BaseOrderRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class MemberOrderRequest extends BaseOrderRequest {
    private String cartId;

    private List<CartOrderItemDto> cartItem;
    private BigDecimal totalPrice; // 전체 금액
    private BigDecimal usedPoint; // 사용 포인트
    private Long memberCouponId; // 사용한 member 쿠폰

}
