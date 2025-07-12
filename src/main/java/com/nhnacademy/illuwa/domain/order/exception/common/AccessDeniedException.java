package com.nhnacademy.illuwa.domain.order.exception.common;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
