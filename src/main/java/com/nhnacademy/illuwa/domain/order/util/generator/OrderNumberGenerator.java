package com.nhnacademy.illuwa.domain.order.util.generator;




import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class OrderNumberGenerator {

    private OrderNumberGenerator() {
        // 유틸 클래스면 이렇게 생성자를 private 처리하더라구요...
    }

    public static String generateOrderNumber(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentTime = time.format(formatter);
        String randomNumber = generateRandomNumber();
        return currentTime  + "-" + randomNumber;
    }

    public static String generateRandomNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
