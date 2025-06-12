package com.nhnacademy.illuwa.domain.coupons.service;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.*;

import java.util.List;

public interface CouponPolicyService {
    // 정책 생성
    CouponPolicyCreateResponse createPolicy(CouponPolicyCreateRequest request);
    // 각 정책의 Id를 기준으로 단건 조회
    CouponPolicyResponse getPolicyById(Long id);
    // 각 정책의 코드를 기준으로 단건 조회
    CouponPolicyResponse getPolicyByCode(String code);
    // 모든 정책 조회
    List<CouponPolicyResponse> getAllPolicies();
    // 정책 수정
    CouponPolicyUpdateResponse updatePolicy(String code, CouponPolicyUpdateRequest request);
    // 정책 삭제 (= 실 삭제가 아닌 상태 값(비활성화 처리)
    void deletePolicy(String code);

//    void createBatch(List<CouponPolicyCreateRequest> policies);
}
