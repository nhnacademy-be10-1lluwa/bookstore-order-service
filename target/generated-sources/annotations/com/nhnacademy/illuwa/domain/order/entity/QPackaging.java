package com.nhnacademy.illuwa.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPackaging is a Querydsl query type for Packaging
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPackaging extends EntityPathBase<Packaging> {

    private static final long serialVersionUID = 1351746355L;

    public static final QPackaging packaging = new QPackaging("packaging");

    public final BooleanPath active = createBoolean("active");

    public final NumberPath<Long> packagingId = createNumber("packagingId", Long.class);

    public final StringPath packagingName = createString("packagingName");

    public final NumberPath<java.math.BigDecimal> packagingPrice = createNumber("packagingPrice", java.math.BigDecimal.class);

    public QPackaging(String variable) {
        super(Packaging.class, forVariable(variable));
    }

    public QPackaging(Path<? extends Packaging> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPackaging(PathMetadata metadata) {
        super(Packaging.class, metadata);
    }

}

