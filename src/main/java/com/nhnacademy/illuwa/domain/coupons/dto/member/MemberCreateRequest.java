package com.nhnacademy.illuwa.domain.coupons.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberCreateRequest {

    @NotNull
    private LocalDate birth;

    @NotBlank
    private String email;

    @NotBlank
    private String name;
}
