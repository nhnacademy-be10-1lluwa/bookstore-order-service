package com.nhnacademy.illuwa.domain.order.external.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberGradeDto {

    private Long memberId;
    private List<BigDecimal> netOrderAmount;

    @QueryProjection
    public MemberGradeDto(Long memberId, List<BigDecimal> netOrderAmount) {
        this.memberId = memberId;
        this.netOrderAmount = netOrderAmount;
    }
}
