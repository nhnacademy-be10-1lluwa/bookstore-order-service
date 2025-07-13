package com.nhnacademy.illuwa.domain.order.dto.order.guest;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestCreateRequest {

    @NotBlank
    String guestId;

    @Positive
    long orderId;

    @NotBlank
    String orderNumber;

    @NotBlank
    String orderPassword;

    @NotBlank
    String name;

    @Email
    @NotBlank
    String email;

    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
            message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
    String contact;


    public static GuestCreateRequest fromGuestOrderRequestDirect(GuestOrderRequestDirect request, Order order) {
        return GuestCreateRequest.builder()
                .guestId(order.getGuestId())
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .orderPassword(request.getOrderPassword())
                .name(request.getName())
                .email(request.getEmail())
                .contact(request.getContact())
                .build();
    }
}
