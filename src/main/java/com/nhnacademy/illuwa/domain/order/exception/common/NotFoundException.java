package com.nhnacademy.illuwa.domain.order.exception.common;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private long id;
    private String orderNumber;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, long id) {
        super(message + id);
        this.id = id;
    }

    public NotFoundException(String message, String orderNumber) {
        super(message + orderNumber);
        this.orderNumber = orderNumber;
    }
}
