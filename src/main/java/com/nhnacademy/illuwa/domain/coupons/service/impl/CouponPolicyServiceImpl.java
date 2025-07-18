package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyExistsByCodeException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.service.CouponPolicyService;
import com.nhnacademy.illuwa.domain.coupons.strategy.registry.DiscountPolicyStrategyRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponPolicyServiceImpl implements CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final DiscountPolicyStrategyRegistry discountPolicyStrategyRegistry;

    @Override
    public CouponPolicyCreateResponse createPolicy(CouponPolicyCreateRequest request) {

        // 할인타입 검증
        discountPolicyStrategyRegistry
                .getStrategy(request.getDiscountType())
                .discountValidate(request);

        if (couponPolicyRepository.existsByCode(request.getCode())) {
            throw new CouponPolicyExistsByCodeException("해당 정책코드는 중복으로 인해 사용이 불가능합니다.");
        }

        CouponPolicy couponPolicy = CouponPolicy.builder()
                .code(request.getCode())
                .minOrderAmount(request.getMinOrderAmount())
                .discountAmount(request.getDiscountAmount())
                .discountPercent(request.getDiscountPercent())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .discountType(request.getDiscountType())
                .build();

        CouponPolicy save = couponPolicyRepository.save(couponPolicy);

        return CouponPolicyCreateResponse.fromEntity(save);

    }

    @Override
    @Transactional(readOnly = true)
    public CouponPolicyResponse getPolicyById(Long id) {
        return couponPolicyRepository.findById(id)
                .map(CouponPolicyResponse::fromEntity)
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 ID 입니다. -> " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public CouponPolicyResponse getPolicyByCode(String code) {

        return couponPolicyRepository.findByCode(code)
                .map(CouponPolicyResponse::fromEntity)
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 코드입니다. -> " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponPolicyResponse> getAllPolicies() {
        return couponPolicyRepository.findAll().stream()
                .map(CouponPolicyResponse::fromEntity)
                .toList();
    }

    @Override
    public CouponPolicyUpdateResponse updatePolicy(String code, CouponPolicyUpdateRequest request) {
        CouponPolicy policy = couponPolicyRepository.findByCode(code)
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 코드입니다. -> + " + code));

        request.updateTo(policy);

        return CouponPolicyUpdateResponse.fromEntity(policy);
    }

    @Override
    public void deletePolicy(String code) {

        CouponPolicy couponPolicy = couponPolicyRepository.findByCode(code)
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 코드입니다. -> + " + code));
        couponPolicy.setStatus(CouponStatus.INACTIVE);
    }

}
