package com.nhnacademy.illuwa.domain.order.entity.types;

public enum OrderStatus {

    AwaitingPayment,  // 결제 대기
    Pending,          // 주문 처리 대기 (예: 주문 접수 상태)
    Shipped,          // 배송 중
    Delivered,        // 배송 완료
    Confirmed,        // 주문 확정 (구매자 확인)
    Returned,         // 반품
    Cancelled         // 주문 취소
}
