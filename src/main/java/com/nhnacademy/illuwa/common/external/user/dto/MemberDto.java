package com.nhnacademy.illuwa.common.external.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberDto {
    @JsonProperty("memberId")
    private Long memberId;
    private String name;
    private String email;
    private LocalDate birth;
}
