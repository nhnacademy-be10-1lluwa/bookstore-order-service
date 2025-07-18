package com.nhnacademy.illuwa.domain.coupons.strategy.registry;

import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.strategy.DiscountPolicyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component // 스프링 빈으로 등록 (자동으로 DI/주입 가능)
public class DiscountPolicyStrategyRegistry {
    private final Map<DiscountType, DiscountPolicyStrategy> registry;

    // 생성자에서 모든 DiscountPolicyStrategy 구현체 리스트로 받아옴
    @Autowired
    public DiscountPolicyStrategyRegistry(List<DiscountPolicyStrategy> strategies) {
        // 리스트를 Map<DiscountType, DiscountPolicyStrategy>로 변환해 registry에 저장
        registry = strategies.stream()
                .collect(Collectors.toMap(DiscountPolicyStrategy::getType,
                        s -> s));
    }

    // 할인 타입이 주어지면, 맞는 전략(검증자)을 반환, 없으면 예외처리
    public DiscountPolicyStrategy getStrategy(DiscountType type) {
        return Optional.ofNullable(registry.get(type))
                .orElseThrow(() -> new CouponPolicyNotFoundException("지원하지 않는 할인 정책입니다."));
    }
}
