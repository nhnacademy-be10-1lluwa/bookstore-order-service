package com.nhnacademy.illuwa.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReturnRequest is a Querydsl query type for ReturnRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReturnRequest extends EntityPathBase<ReturnRequest> {

    private static final long serialVersionUID = 787376879L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReturnRequest returnRequest = new QReturnRequest("returnRequest");

    public final StringPath memberId = createString("memberId");

    public final QOrder order;

    public final DateTimePath<java.time.ZonedDateTime> requestedAt = createDateTime("requestedAt", java.time.ZonedDateTime.class);

    public final DateTimePath<java.time.ZonedDateTime> returnedAt = createDateTime("returnedAt", java.time.ZonedDateTime.class);

    public final NumberPath<Long> returnId = createNumber("returnId", Long.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason> returnReason = createEnum("returnReason", com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason.class);

    public final NumberPath<java.math.BigDecimal> shippingFeeDeducted = createNumber("shippingFeeDeducted", java.math.BigDecimal.class);

    public final EnumPath<com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus> status = createEnum("status", com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus.class);

    public QReturnRequest(String variable) {
        this(ReturnRequest.class, forVariable(variable), INITS);
    }

    public QReturnRequest(Path<? extends ReturnRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReturnRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReturnRequest(PathMetadata metadata, PathInits inits) {
        this(ReturnRequest.class, metadata, inits);
    }

    public QReturnRequest(Class<? extends ReturnRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

