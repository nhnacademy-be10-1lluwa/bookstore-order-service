package com.nhnacademy.illuwa.domain.order;

public enum ReturnStatus {
    Requested, // 반품 요청됨
    Approved, // 반품 승인됨
    Rejected, // 반품 거절됨
    Refunded // 환불 완료
}
