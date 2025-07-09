package com.nhnacademy.illuwa.domain.coupons.service;

import com.nhnacademy.illuwa.domain.coupons.dto.coupon.*;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;

import java.util.List;

public interface CouponService {
    // 쿠폰 생성
    CouponCreateResponse createCoupon(CouponCreateRequest request);

    // 특정 도서 쿠폰 생성
//    CouponCreateResponse createCouponByBookTitle(String bookTitle, CouponCreateRequest request);

    // 쿠폰 단건 조회 {id}
    CouponResponse getCouponById(Long id);

    // 쿠폰 목록 조회 {code} = {정책코드}
    List<CouponResponse> getCouponsByPolicyCode(String code);

    // 쿠폰 목록 조회 {type}
    List<CouponResponse> getCouponsByType(CouponType type);

    // 쿠폰 목록 조회 {name}
    List<CouponResponse> getCouponsByName(String name);

    // 쿠폰 전체 조회
    List<CouponResponse> getAllCoupons();

    // 쿠폰 수정
    CouponUpdateResponse updateCoupon(Long id, CouponUpdateRequest request);

    // 쿠폰 삭제
    void deleteCoupon(Long id);
}
