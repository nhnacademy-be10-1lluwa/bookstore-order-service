package com.nhnacademy.illuwa.domain.coupons.strategy;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;

/**
 * 할인 정책(타입)에 따른 유효성 검사를 위한 설계도 역할
 * 각각의 할인 정책(금액, 퍼센트, ....)등이 자기만의 유효성 검증 로직을 구현하도록 강제
 */
public interface DiscountPolicyStrategy {
    // 내가 담당하는 할인 타입이 무엇인지 리턴
    DiscountType getType();
    // 할인 정책 생성 요청에 대한 '할인 타입별' 유효성 체크
    void discountValidate(CouponPolicyCreateRequest request);
}
