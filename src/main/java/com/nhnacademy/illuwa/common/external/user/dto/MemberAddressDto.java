package com.nhnacademy.illuwa.common.external.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressDto {
    private Long addressId;
    private String addressName;
    private String recipient;
    private String contact;
    private String addressDetail;
    private boolean isDefault;
}
