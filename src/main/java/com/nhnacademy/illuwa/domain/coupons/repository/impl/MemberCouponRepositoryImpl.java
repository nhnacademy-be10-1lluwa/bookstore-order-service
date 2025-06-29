package com.nhnacademy.illuwa.domain.coupons.repository.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.QMemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.QCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.QCouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.QMemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.repository.custom.CouponMemberQuerydslRepository;
import com.nhnacademy.illuwa.domain.order.entity.QOrder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberCouponRepositoryImpl extends QuerydslRepositorySupport implements CouponMemberQuerydslRepository {

    private final JPAQueryFactory queryFactory;
    private final QCouponPolicy cp = QCouponPolicy.couponPolicy;
    private final QCoupon c = QCoupon.coupon;
    private final QMemberCoupon mc = QMemberCoupon.memberCoupon;

    public MemberCouponRepositoryImpl(JPAQueryFactory queryFactory) {
        super(MemberCoupon.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<MemberCouponDiscountDto> findDtoByMemberCouponId(Long memberCouponId) {

        MemberCouponDiscountDto result = queryFactory
                .select(new QMemberCouponDiscountDto(
                        cp.discountAmount,
                        cp.discountPercent))
                .from(mc)
                .join(mc.coupon, c)
                .join(c.policy, cp)
                .where(mc.id.eq(memberCouponId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
