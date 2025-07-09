package com.nhnacademy.illuwa.common.external.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberGradeUpdateRequest {

    private Long memberId;
    private List<BigDecimal> netOrderAmount;

    @QueryProjection
    public MemberGradeUpdateRequest(Long memberId, List<BigDecimal> netOrderAmount) {
        this.memberId = memberId;
        this.netOrderAmount = netOrderAmount;
    }
}
