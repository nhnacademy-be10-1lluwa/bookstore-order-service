package com.nhnacademy.illuwa.domain.coupons.exception;

import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.BadRequestException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyInactiveException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.memberCoupon.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

// 중복코드 후에 리팩토링 예정 -> error response
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "code", "BAD_REQUEST",
                        "message", ex.getMessage()
                ));
    }
    // 정책 존재 X
    @ExceptionHandler(CouponPolicyNotFoundException.class)
    public ResponseEntity<?> handleCouponPolicyNotFound(CouponPolicyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "code", "COUPON_POLICY_NOT_FOUND",
                        "message", ex.getMessage()
                ));
    }

    // DB 런타임 에러 -> 중복
    // 다른 에러처럼 커스텀 에러로 처리안한 이유는?
    // -> 유니크 제약 조건 위반은 DB 쿼리 실행 시에 터지기 떄문에
    // 서비스 로직에서 if(중복체크)문으로 체크해서 미리 예외를 던지기가 어렵기 떄문
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "error", HttpStatus.CONFLICT.getReasonPhrase(),
                        "code", "COUPON_POLICY_CODE_DUPLICATED",
                        "message", "이미 존재하는 쿠폰 정책 코드입니다."
                ));
    }

    // 정책 비활성화
    @ExceptionHandler(CouponPolicyInactiveException.class)
    public ResponseEntity<?> handleCouponPolicyInactiveException(CouponPolicyInactiveException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "error", HttpStatus.CONFLICT.getReasonPhrase(),
                        "code", "COUPON_POLICY_INACTIVE",
                        "message", ex.getMessage()
                ));
    }

    // 쿠폰 존재 X
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<?> handleCouponNotFoundException(CouponNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "code", "COUPON_NOT_FOUND",
                        "message", ex.getMessage()
                ));
    }

    // 쿠폰 유효기간 만료
    @ExceptionHandler(MemberCouponExpiredException.class)
    public ResponseEntity<?> handleMemberCouponExpiredException(MemberCouponExpiredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "error", HttpStatus.CONFLICT.getReasonPhrase(),
                        "code", "COUPON_EXPIRED",
                        "message", ex.getMessage()

                ));
    }

    // 쿠폰 중복 발급
    @ExceptionHandler(MemberCouponInactiveException.class)
    public ResponseEntity<?> handleMemberCouponInactiveException(MemberCouponInactiveException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "error", HttpStatus.CONFLICT.getReasonPhrase(),
                        "code", "COUPON_ALREADY_ISSUED",
                        "message", ex.getMessage()
                ));
    }

    // 이미 사용한 쿠폰
    @ExceptionHandler(MemberCouponIsUsed.class)
    public ResponseEntity<?> handleMemberCouponIsUsed(MemberCouponIsUsed ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "error", HttpStatus.CONFLICT.getReasonPhrase(),
                        "code", "COUPON_ALREADY_USED",
                        "message", ex.getMessage()
                ));
    }

    // 쿠폰 수량 마감
    @ExceptionHandler(MemberCouponQuantityFinishException.class)
    public ResponseEntity<?> handleMemberCouponQuantityFinishException(MemberCouponQuantityFinishException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.CONFLICT.value(),
                        "error", HttpStatus.CONFLICT.getReasonPhrase(),
                        "code", "COUPON_OUT_OF_STOCK",
                        "message", ex.getMessage()
                ));
    }

    // 쿠폰 존재 X
    @ExceptionHandler(MemberCouponNotFoundException.class)
    public ResponseEntity<?> handleMemberCouponNotFoundException(MemberCouponNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.NOT_FOUND.value(),
                        "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                        "code", "COUPON_NOT_FOUND",
                        "message", ex.getMessage()
                ));
    }
}
