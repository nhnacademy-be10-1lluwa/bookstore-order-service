package com.nhnacademy.illuwa.domain.coupons.repository.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.QMemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.QMemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.QCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.QCouponPolicy;
import com.nhnacademy.illuwa.domain.coupons.entity.QMemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.repository.custom.CouponMemberQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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
                        cp.discountPercent,
                        cp.maxDiscountAmount))
                .from(mc)
                .join(mc.coupon, c)
                .join(c.policy, cp)
                .where(mc.id.eq(memberCouponId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<MemberCouponDto> findAvailableCouponsWelcome(Long memberId, CouponType couponType) {
        QMemberCoupon mc = QMemberCoupon.memberCoupon;
        QCoupon c = QCoupon.coupon;
        QCouponPolicy cp = QCouponPolicy.couponPolicy;

        return queryFactory
                .select(new QMemberCouponDto(
                        mc.id,
                        c.id,
                        c.couponName,
                        c.couponType,
                        cp.discountAmount,
                        cp.discountPercent
                ))
                .from(mc)
                .join(mc.coupon, c)
                .join(c.policy, cp)
                .where(
                        mc.memberId.eq(memberId) // 해당 회원
                                .and(mc.used.eq(false)) // 미사용 쿠폰
                                .and(c.couponType.eq(couponType))
                                .and(cp.status.eq(CouponStatus.ACTIVE)) // 정책 활성화
                                .and(mc.expiresAt.goe(LocalDate.now())) // 유효기간 내
                )
                .fetch();
    }

    @Override
    public List<MemberCouponDto> findAvailableCouponsForBook(Long memberId, Long bookId, CouponType couponType) {
        QMemberCoupon mc = QMemberCoupon.memberCoupon;
        QCoupon c = QCoupon.coupon;
        QCouponPolicy cp = QCouponPolicy.couponPolicy;

        return queryFactory
                .select(new QMemberCouponDto(
                        mc.id,
                        c.id,
                        c.couponName,
                        c.couponType,
                        cp.discountAmount,
                        cp.discountPercent
                ))
                .from(mc)
                .join(mc.coupon, c)
                .join(c.policy, cp)
                .where(
                        mc.memberId.eq(memberId) // 해당 회원
                                .and(mc.used.eq(false)) // 미사용 쿠폰
                                .and(c.bookId.eq(bookId)) // 이 도서에만 적용되는 쿠폰
                                .and(c.couponType.eq(couponType))
                                .and(cp.status.eq(CouponStatus.ACTIVE)) // 정책 활성화
                                .and(mc.expiresAt.goe(LocalDate.now())) // 유효기간 내
                )
                .fetch();
    }

    public List<MemberCouponDto> findAvailableCouponsForCategory(Long memberId, Long categoryId, CouponType couponType) {
        QMemberCoupon mc = QMemberCoupon.memberCoupon;
        QCoupon c = QCoupon.coupon;
        QCouponPolicy cp = QCouponPolicy.couponPolicy;

        return queryFactory
                .select(new QMemberCouponDto(
                        mc.id,
                        c.id,
                        c.couponName,
                        c.couponType,
                        cp.discountAmount,
                        cp.discountPercent
                ))
                .from(mc)
                .join(mc.coupon, c)
                .join(c.policy, cp)
                .where(
                        mc.memberId.eq(memberId) // 해당 회원
                                .and(mc.used.eq(false)) // 미사용 쿠폰
                                .and(c.categoryId.eq(categoryId)) // 이 카테고리에만 적용되는 쿠폰
                                .and(c.couponType.eq(couponType))
                                .and(cp.status.eq(CouponStatus.ACTIVE)) // 정책 활성화
                                .and(mc.expiresAt.goe(LocalDate.now())) // 유효기간 내
                )
                .fetch();
    }

}
