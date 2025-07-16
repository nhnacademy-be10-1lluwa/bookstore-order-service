package com.nhnacademy.illuwa.common.external.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberGradeUpdateRequest {

    private long memberId;
    private BigDecimal netOrderAmount;


    @QueryProjection
    public MemberGradeUpdateRequest(long memberId, BigDecimal netOrderAmount) {
        this.memberId = memberId;
        this.netOrderAmount = netOrderAmount;
    }
}
