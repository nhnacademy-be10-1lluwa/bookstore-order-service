package com.nhnacademy.illuwa.domain.order.exception.common;

import lombok.Getter;

@Getter
public class NotFoundStringException extends RuntimeException {
    private final String number;

    public NotFoundStringException(String message, String number) {
        super(message);
        this.number = number;
    }


}
