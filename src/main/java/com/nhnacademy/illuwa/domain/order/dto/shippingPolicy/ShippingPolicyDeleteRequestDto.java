package com.nhnacademy.illuwa.domain.order.dto.shippingPolicy;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingPolicyDeleteRequestDto {

    private boolean active;
}
