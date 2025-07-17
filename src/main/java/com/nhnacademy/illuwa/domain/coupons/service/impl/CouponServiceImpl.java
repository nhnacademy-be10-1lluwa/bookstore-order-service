package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.DuplicateCouponException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.BadRequestException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyInactiveException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import com.nhnacademy.illuwa.domain.coupons.strategy.registry.CouponTypeValidatorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final ProductApiClient productApiClient;
    private final CouponTypeValidatorRegistry couponTypeValidatorRegistry;

    /**
     * 해당 createCoupon에 대한 문제점
     * SRP(단일 책임 원칙) 위배 -> createCoupon이라는 메서드가 너무 많은 역할을 맡고 있음
     * 담당 책임 :
     * 1. 정책 조회 & 예외 처리
     * 2. 중복 쿠폰 체크
     * 3. 쿠폰 타입에 따른 외부 데이터 조회 검증 (book/category)
     * 4. 유효기간 검증
     * 5. 정책 상태 체크
     * 6. 엔티티 생성 및 저장
     * 7. DTO 변환 및 반환
     * = 비즈니스 로직 + 유효성 검사 + 외부 시스템 연동이 짬뽕적으로 섞임
     */

    @Override
    public CouponCreateResponse createCoupon(CouponCreateRequest request) {

        // 1 -> 정책 코드 확인
        CouponPolicy policy = couponPolicyRepository.findByCode(request.getPolicyCode())
                .orElseThrow(() -> new CouponPolicyNotFoundException("해당 정책코드는 존재하지 않습니다."));

        // 2 -> 쿠폰 중복 여부 확인
        if (couponRepository.existsByCouponNameAndPolicy_CodeAndCouponType(request.getCouponName(), request.getPolicyCode(), request.getCouponType())) {
            throw new DuplicateCouponException("이미 존재하는 쿠폰 입니다.");
        }

        // 3 -> 쿠폰 타입에 따른 검증 로직
        couponTypeValidatorRegistry
                .getValidator(request.getCouponType())
                .validate(request, productApiClient);

        // 4 -> 쿠폰 생성 날짜에 대한 검증 로직
        if (request.getValidFrom().isAfter(request.getValidTo())) {
            throw new BadRequestException("유효 시작일은 유효 종료일 이전이어야 합니다.");
        }


        // 5 -> 정책이 활성화인지 비활성인지 체크
        if (!policy.getStatus().equals(CouponStatus.INACTIVE)) {
            Coupon coupon = Coupon.builder()
                    .couponName(request.getCouponName())
                    .policy(policy)
                    .validFrom(request.getValidFrom())
                    .validTo(request.getValidTo())
                    .couponType(request.getCouponType())
                    .comment(request.getComment())
                    .conditions(request.getConditions())
                    .issueCount(request.getIssueCount())
                    .bookId(request.getBooksId())
                    .categoryId(request.getCategoryId())
                    .build();
            Coupon save = couponRepository.save(coupon);
            return CouponCreateResponse.fromEntity(save);
        } else { // 비활성일시 에러 발생
            throw new CouponPolicyInactiveException("쿠폰 정책 상태가 비활성화이므로 생성 불가합니다.");
        }
    }


    /**
     * 1. JPA(하이버네이트) 영속성 컨텍스트 최적화
     * -> 쓰기"관련 기능 (Dirty Checking, 스냅샷 등) 비활성화
     * -> 엔티티 변경 감지(변수 변경 추적)을 아예 안함 -> 불필요한 오버헤드 감소
     * 2. 데이터베이스(물리적 DB) 쿼리 최적화
     * -> 일부 DB(MySQL, Oracle등) 쓰기 락, 트랜잭션 로그 기록 등을 더 가볍게 처리
     * -> 쓸 일 없는 변경/커밋에 대한 처리 자체를 줄여줌
     * 3. 불필요한 Flush, Lock 방지
     * -> readOnly 트랜잭션은 flush(쓰기 쿼리 동기화)처리 X, 성능이 더 좋고, 트랜잭션 Lock도 최소화
     */

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {

        return couponRepository.findById(id)
                .map(CouponResponse::fromEntity)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다. -> " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponsByPolicyCode(String code) {
        return couponRepository.findByPolicy_Code(code).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponsByType(CouponType type) {
        return couponRepository.findCouponByCouponType(type).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getCouponsByName(String name) {
        return couponRepository.findByCouponName(name).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    public CouponUpdateResponse updateCoupon(Long id, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰ID 입니다. ->" + id));

        LocalDate validFrom = request.getValidFrom() != null ? request.getValidFrom() : coupon.getValidFrom();
        LocalDate validTo = request.getValidTo() != null ? request.getValidTo() : coupon.getValidTo();

        if (validFrom.isAfter(validTo)) {
            throw new BadRequestException("유효 시작일은 유효 종료일 이전이어야 합니다.");
        }

        if (request.getCouponName() != null) {
            coupon.setCouponName(request.getCouponName());
        }
        if (request.getValidFrom() != null) {
            coupon.setValidFrom(request.getValidFrom());
        }
        if (request.getValidTo() != null) {
            coupon.setValidTo(request.getValidTo());
        }
        if (request.getCouponType() != null) {
            coupon.setCouponType(request.getCouponType());
        }
        if (request.getComment() != null) {
            coupon.setComment(request.getComment());
        }
        if (request.getIssueCount() != null) {
            coupon.setIssueCount(request.getIssueCount());
        }

        return CouponUpdateResponse.fromEntity(coupon);
    }

    // 추후에 소프트 삭제를 할지 영구 삭제를 할지 고민
    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰 ID 입니다. -> " + id));
        couponRepository.delete(coupon);
    }

}
