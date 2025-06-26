package com.nhnacademy.illuwa.domain.order.external.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPointDto {

    private BigDecimal point;
}
