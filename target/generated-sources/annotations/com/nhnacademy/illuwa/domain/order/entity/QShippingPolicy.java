package com.nhnacademy.illuwa.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QShippingPolicy is a Querydsl query type for ShippingPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShippingPolicy extends EntityPathBase<ShippingPolicy> {

    private static final long serialVersionUID = -552659248L;

    public static final QShippingPolicy shippingPolicy = new QShippingPolicy("shippingPolicy");

    public final BooleanPath active = createBoolean("active");

    public final NumberPath<java.math.BigDecimal> fee = createNumber("fee", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> minAmount = createNumber("minAmount", java.math.BigDecimal.class);

    public final NumberPath<Long> shippingPolicyId = createNumber("shippingPolicyId", Long.class);

    public QShippingPolicy(String variable) {
        super(ShippingPolicy.class, forVariable(variable));
    }

    public QShippingPolicy(Path<? extends ShippingPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QShippingPolicy(PathMetadata metadata) {
        super(ShippingPolicy.class, metadata);
    }

}

