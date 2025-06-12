package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long> {

    // 전체 조회
    // findAll

    // id로 정책 조회하기
    ShippingPolicy findByShippingPolicyId(long shippingPolicyId);
}
