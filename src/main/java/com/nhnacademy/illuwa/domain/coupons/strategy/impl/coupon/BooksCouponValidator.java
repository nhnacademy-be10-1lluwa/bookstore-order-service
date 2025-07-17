package com.nhnacademy.illuwa.domain.coupons.strategy.impl.coupon;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.strategy.CouponTypeValidator;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BooksCouponValidator implements CouponTypeValidator {
    @Override
    public CouponType getType() {
        return CouponType.BOOKS;
    }

    @Override
    public void validate(CouponCreateRequest request, ProductApiClient productApi) {
        // 필드 검사
        if (Objects.isNull(request.getBooksId())) {
            throw new BadRequestException("도서 할인 쿠폰은 bookId가 필요합니다.");
        }
        // api 연동 검사
        productApi.getBookById(request.getBooksId())
                .orElseThrow(() -> new NotFoundException("해당 도서에 대한 정보를 가져올 수 없습니다."));
    }
}
