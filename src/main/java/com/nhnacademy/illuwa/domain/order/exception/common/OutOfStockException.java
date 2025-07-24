package com.nhnacademy.illuwa.domain.order.exception.common;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(Long bookId) {
        super("재고 부족 : " + bookId);
    }
}
