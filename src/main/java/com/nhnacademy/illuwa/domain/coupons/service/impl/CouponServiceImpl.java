package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.exception.coupon.CouponNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyInactiveException;
import com.nhnacademy.illuwa.domain.coupons.exception.couponPolicy.CouponPolicyNotFoundException;
import com.nhnacademy.illuwa.domain.coupons.external.book.BookApiClient;
import com.nhnacademy.illuwa.domain.coupons.external.book.BookDto;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponPolicyRepository;
import com.nhnacademy.illuwa.domain.coupons.repository.CouponRepository;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final BookApiClient bookApiClient;

    @Override
    public CouponCreateResponse createCoupon(CouponCreateRequest request) {
        Long bookId = null; // 도서 ID
        Long categoryId = null; // 카테고리 ID
        String categoryName = null;

        // 1 -> 정책 코드 확인
        CouponPolicy policy = couponPolicyRepository.findByCode(request.getPolicyCode())
                .orElseThrow(() -> new CouponPolicyNotFoundException("해당 정책코드는 존재하지 않습니다."));

        // 2 -> 쿠폰 타입 검증 로직
        if (request.getCouponType() == CouponType.BOOKS) {
            if (Objects.isNull(request.getBooksId())) {
                throw new IllegalArgumentException("도서 할인 쿠폰은 bookId 필드의 값이 필요합니다.");
            } else {
                bookId = bookApiClient.getBookById(request.getBooksId()).getBookId();
            }
        }else if(request.getCouponType() == CouponType.CATEGORY){
            if (Objects.isNull(request.getCategoryId())) {
                throw new IllegalArgumentException("카테고리 할인 쿠폰은 categoryId 필드의 값이 필요합니다.");
            } else {
                categoryId = bookApiClient.getCategoryId(request.getCategoryId()).getCategoryId();
                categoryName = bookApiClient.getCategoryId(request.getCategoryId()).getCategoryName();
            }
        } else {
            bookId = request.getBooksId();
            categoryId = request.getCategoryId();
        }

        System.out.println(categoryName);


        // 3 -> 정책이 활성화인지 비활성인지 체크
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
                    .bookId(bookId)
                    .categoryId(categoryId)
                    .build();
            Coupon save = couponRepository.save(coupon);
            return CouponCreateResponse.fromEntity(save);
        } else { // 비활성일시 에러 발생
            throw new CouponPolicyInactiveException("쿠폰 정책 상태가 비활성화이므로 생성 불가합니다.");
        }
    }



    @Override
    public CouponResponse getCouponById(Long id) {

        return couponRepository.findById(id)
                .map(CouponResponse::fromEntity)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰입니다. -> " + id));
    }

    @Override
    public List<CouponResponse> getCouponsByPolicyCode(String code) {
        return couponRepository.findByPolicy_Code(code).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    public List<CouponResponse> getCouponsByType(CouponType type) {
        return couponRepository.findCouponByCouponType(type).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    public List<CouponResponse> getCouponsByName(String name) {
        return couponRepository.findByCouponName(name).stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponResponse::fromEntity)
                .toList();
    }

    @Override
    public CouponUpdateResponse updateCoupon(Long id, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("존재하지 않는 쿠폰ID 입니다. ->" + id));
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
