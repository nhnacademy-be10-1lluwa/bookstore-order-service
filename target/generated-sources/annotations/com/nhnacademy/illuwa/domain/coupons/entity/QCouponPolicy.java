package com.nhnacademy.illuwa.domain.coupons.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCouponPolicy is a Querydsl query type for CouponPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponPolicy extends EntityPathBase<CouponPolicy> {

    private static final long serialVersionUID = 66152007L;

    public static final QCouponPolicy couponPolicy = new QCouponPolicy("couponPolicy");

    public final StringPath code = createString("code");

    public final DateTimePath<java.time.LocalDateTime> createAt = createDateTime("createAt", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> discountAmount = createNumber("discountAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> discountPercent = createNumber("discountPercent", java.math.BigDecimal.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<java.math.BigDecimal> maxDiscountAmount = createNumber("maxDiscountAmount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minOrderAmount = createNumber("minOrderAmount", java.math.BigDecimal.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus> status = createEnum("status", com.nhnacademy.illuwa.domain.coupons.entity.status.CouponStatus.class);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public QCouponPolicy(String variable) {
        super(CouponPolicy.class, forVariable(variable));
    }

    public QCouponPolicy(Path<? extends CouponPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCouponPolicy(PathMetadata metadata) {
        super(CouponPolicy.class, metadata);
    }

}

