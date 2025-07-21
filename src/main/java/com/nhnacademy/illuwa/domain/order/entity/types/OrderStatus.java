package com.nhnacademy.illuwa.domain.order.entity.types;

public enum OrderStatus {

    AwaitingPayment,  // 결제 대기
    Pending,          // 배송 대기
    Shipped,          // 배송 중
    Delivered,        // 배송 완료
    Confirmed,        // 구매 확정 (구매자 확인)
    Returned,         // 반품
    Cancelled         // 주문 취소
}
