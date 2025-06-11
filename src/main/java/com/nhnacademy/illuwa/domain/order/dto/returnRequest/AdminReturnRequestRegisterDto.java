package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReturnRequestRegisterDto {

    private String returnStatus;
    private ZonedDateTime returnedAt;
}
