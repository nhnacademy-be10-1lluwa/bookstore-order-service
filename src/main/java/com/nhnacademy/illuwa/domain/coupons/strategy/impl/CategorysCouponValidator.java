package com.nhnacademy.illuwa.domain.coupons.strategy.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.strategy.CouponTypeValidator;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CategorysCouponValidator implements CouponTypeValidator {
    @Override
    public CouponType getType() {
        return CouponType.CATEGORY;
    }

    @Override
    public void validate(CouponCreateRequest request, ProductApiClient productApi) {
        // 필드 검사
        if (Objects.isNull(request.getCategoryId())) {
            throw new BadRequestException("카테고리 할인 쿠폰은 categoryId가 필요합니다.");
        }

        productApi.getCategoryById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("해당 카테고리에 대한 정보를 가져올 수 없습니다."));
    }
}
