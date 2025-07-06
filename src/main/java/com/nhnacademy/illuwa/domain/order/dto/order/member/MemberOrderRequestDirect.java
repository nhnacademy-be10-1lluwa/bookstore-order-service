package com.nhnacademy.illuwa.domain.order.dto.order.member;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberOrderRequestDirect {

    private String recipientName;
    private String recipientContact;
    private String readAddress;
    private String detailAddress;
    private LocalDate deliverDate;

    private CartOrderItemDto item;
    private BigDecimal usedPoint;
    private Long memberCouponId;
}
