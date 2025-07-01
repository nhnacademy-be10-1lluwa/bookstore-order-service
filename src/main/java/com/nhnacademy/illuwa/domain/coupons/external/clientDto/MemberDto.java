package com.nhnacademy.illuwa.domain.coupons.external.clientDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberDto {
    @JsonProperty("member_id")
    Long memberId;
    String name;
    String email;
    LocalDate birth;
}
