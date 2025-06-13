package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ActiveShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyUpdateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;

import java.util.List;

public interface ShippingPolicyService {

    // 배송비 정책 전체 조회 - 사용하지 않을 듯(?)
    List<ActiveShippingPolicyDto> getAllShippingPolicy();

    // 활성화 배송비 정책 조회
    List<ActiveShippingPolicyDto> getShippingPolicyByActive();

    // 단일 정책 조회
    ActiveShippingPolicyDto getShippingPolicy(String shippingPolicyId);

    // 정책 추가
    ShippingPolicy addShippingPolicy(ShippingPolicyCreateRequestDto shippingPolicyCreateDto);

    // 정책 삭제 (활성화 여부 false 로 변경)
    int removeShippingPolicy(String shippingPolicyId);

    // 정책 수정 (해당 옵션은 active = false 로 처리, 새로운 포장 옵션 추가)
    ShippingPolicy updateShippingPolicy(String shippingPolicyId, ShippingPolicyCreateRequestDto shippingPolicyCreateDto);
}
