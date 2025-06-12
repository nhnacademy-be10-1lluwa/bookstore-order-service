package com.nhnacademy.illuwa.domain.order.exception.packaging;

import lombok.Getter;

@Getter
public class PackagingNotFoundException extends RuntimeException{
    private final long id;
    public PackagingNotFoundException(long id) {
        super("포장 옵션을 찾을 수 없습니다." + id);
        this.id = id;
    }
}

