package com.nhnacademy.illuwa.common.external.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressDto {
    private Long memberAddressId; // 식별자
    private String addressName; // 주소 별칭
    private String recipientName; // 수령인
    private String recipientContact; // 수령인 연락처
    private String address; // 주소
    private String postCode; // 우편 번호
    private String detailAddress; // 상세 주소
    private Boolean defaultAddress; // 기본 주소 - true : 기본주소
}
