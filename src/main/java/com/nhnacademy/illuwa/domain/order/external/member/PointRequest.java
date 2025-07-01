package com.nhnacademy.illuwa.domain.order.external.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PointRequest {

    private Long memberId;
    private BigDecimal pointAmount;

}
