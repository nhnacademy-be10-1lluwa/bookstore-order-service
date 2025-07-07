package com.nhnacademy.illuwa.domain.order.util.generator;




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

    // 비회원 번호 생성 (20자리, 숫자만)
    public static String generateGuestId() {
        StringBuilder uuid = new StringBuilder(UUID.randomUUID().toString().replaceAll("[^0-9]", ""));
        while (uuid.length() < 20) {
            uuid.append("0"); // pad if too short
        }
        return uuid.substring(0, 20);
    }
}