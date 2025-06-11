package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.service.CouponPolicyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponPolicyServiceImpl implements CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    @Override
    public CouponPolicyCreateResponse createPolicy(CouponPolicyCreateRequest request) {
        CouponPolicy couponPolicy = CouponPolicy.builder()
                .code(request.getCode())
                .minOrderAmount(request.getMinOrderAmount())
                .discountAmount(request.getDiscountAmount())
                .discountPercent(request.getDiscountPercent())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .build();

        CouponPolicy save = couponPolicyRepository.save(couponPolicy);

        return CouponPolicyCreateResponse.fromEntity(save);

    }

    @Override
    public CouponPolicyResponse getPolicyByCode(String code) {

        return couponPolicyRepository.findByCode(code)
                .map(CouponPolicyResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다. -> " + code));
    }

    @Override
    public List<CouponPolicyResponse> getAllPolicies() {
        return couponPolicyRepository.findAll().stream()
                .map(CouponPolicyResponse::fromEntity)
                .toList();
    }

    @Override
    public CouponPolicyUpdateResponse updatePolicy(String code, CouponPolicyUpdateRequest request) {
        CouponPolicy policy = couponPolicyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다. + " + code));

        if (request.getMinOrderAmount() != null) {
            policy.setMinOrderAmount(request.getMinOrderAmount());
        }

        if (request.getDiscountAmount() != null) {
            policy.setDiscountAmount(request.getDiscountAmount());
        }

        if (request.getDiscountPercent() != null) {
            policy.setDiscountPercent(request.getDiscountPercent());
        }

        if (request.getMaxDiscountAmount() != null) {
            policy.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }

        if (request.getStatus() != null) {
            policy.setStatus(request.getStatus());
        }

        return CouponPolicyUpdateResponse.fromEntity(policy);
    }

    @Override
    public void deletePolicy(String code) {

        CouponPolicy couponPolicy = couponPolicyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다. + " + code));
        couponPolicy.setStatus(CouponStatus.INACTIVE);
    }

//    @Override
//    public void createBatch(List<CouponPolicyCreateRequest> policies) {
//        for (CouponPolicyCreateRequest req : policies) {
//            createPolicy(req); // 단건 생성 메서드를 사용
//        }
//    }
}
