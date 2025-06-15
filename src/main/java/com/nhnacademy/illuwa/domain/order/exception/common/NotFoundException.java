package com.nhnacademy.illuwa.domain.order.exception.common;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final long id;
    public NotFoundException(String message, long id) {
        super(message + id);
        this.id = id;
    }
}
