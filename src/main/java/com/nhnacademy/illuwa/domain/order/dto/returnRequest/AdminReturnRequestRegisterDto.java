package com.nhnacademy.illuwa.domain.order.dto.returnRequest;

import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReturnRequestRegisterDto {

    private ReturnStatus returnStatus;
}
