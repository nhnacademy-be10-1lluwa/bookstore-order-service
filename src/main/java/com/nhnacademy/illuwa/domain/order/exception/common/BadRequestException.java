package com.nhnacademy.illuwa.domain.order.exception.common;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

}
