package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.service.CouponPolicyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponPolicyServiceImpl implements CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    @Override
    public CouponPolicyCreateResponse createPolicy(CouponPolicyCreateRequest request) {
        if (request.getDiscountType() == DiscountType.AMOUNT) {
            if (Objects.isNull(request.getDiscountAmount())) {
                throw new IllegalArgumentException("금액 할인 정책은 discountAmount 값이 필요합니다.");
            }
            if (request.getDiscountPercent() != null) {
                throw new IllegalArgumentException("금액 할인 정책은 discountPercent 값을 입력하면 안됩니다.");
            }
        } else {
            if (Objects.isNull(request.getDiscountPercent())) {
                throw new IllegalArgumentException("퍼센트 할인 정책은 discountPercent 값이 필요합니다.");
            }
            if (Objects.nonNull(request.getDiscountAmount())) {
                throw new IllegalArgumentException("퍼센트 할인 정책은 discountAmount 값을 입력하면 안됩니다.");
            }
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
    public CouponPolicyResponse getPolicyById(Long id) {
        return couponPolicyRepository.findById(id)
                .map(CouponPolicyResponse::fromEntity)
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 ID 입니다. -> " + id));
    }

    @Override
    public CouponPolicyResponse getPolicyByCode(String code) {

        return couponPolicyRepository.findByCode(code)
                .map(CouponPolicyResponse::fromEntity)
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 코드입니다. -> " + code));
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
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 코드입니다. -> + " + code));

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
                .orElseThrow(() -> new CouponPolicyNotFoundException("존재하지 않는 쿠폰 코드입니다. -> + " + code));
        couponPolicy.setStatus(CouponStatus.INACTIVE);
    }

//    @Override
//    public void createBatch(List<CouponPolicyCreateRequest> policies) {
//        for (CouponPolicyCreateRequest req : policies) {
//            createPolicy(req); // 단건 생성 메서드를 사용
//        }
//    }
}
