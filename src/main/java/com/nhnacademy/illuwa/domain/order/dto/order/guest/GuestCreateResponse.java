package com.nhnacademy.illuwa.domain.order.dto.order.guest;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCreateResponse {
    String guestId;
    long orderId;
    String orderNumber;
    String name;
    String email;
    String contact;
}
