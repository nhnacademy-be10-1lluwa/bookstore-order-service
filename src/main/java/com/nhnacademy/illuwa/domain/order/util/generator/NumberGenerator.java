package com.nhnacademy.illuwa.domain.order.util.generator;


import com.github.f4b6a3.ulid.UlidCreator;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class NumberGenerator {

    private NumberGenerator() {
        // 유틸 클래스면 이렇게 생성자를 private 처리하더라구요...
    }

    public static String generateOrderNumber(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentTime = time.format(formatter);
        String randomNumber = generateRandomNumber();
        return currentTime + "-" + randomNumber;
    }

    public static String generateRandomNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    // 비회원 번호 생성 (ULID 26자)
    public static String generateGuestId() {
        return UlidCreator.getUlid().toString();   // 예: 01HACVG2D8VY7P1JEP2JRM9R3N 26자리 비회원
    }
}