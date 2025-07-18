package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.returnRequest.*;
import com.nhnacademy.illuwa.domain.order.entity.QOrder;
import com.nhnacademy.illuwa.domain.order.entity.QReturnRequest;
import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import com.nhnacademy.illuwa.domain.order.repository.custom.ReturnRequestQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReturnRequestRepositoryImpl extends QuerydslRepositorySupport implements ReturnRequestQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    private final QReturnRequest returnRequest = QReturnRequest.returnRequest;

    @Autowired
    public ReturnRequestRepositoryImpl(JPAQueryFactory queryFactory) {
        super(ReturnRequest.class);
        this.queryFactory = queryFactory;
    }

    // 전체 조회
    @Override
    public List<ReturnRequestListResponseDto> findAllDtos() {
        return queryFactory
                .select(new QReturnRequestListResponseDto(
                        returnRequest.returnId,
                        returnRequest.requestedAt,
                        returnRequest.returnedAt,
                        returnRequest.returnReason,
                        returnRequest.status,
                        returnRequest.order.orderId))
                .from(returnRequest)
                .fetch();
    }

    // 처리 대기 중인 반품 요청 조회
    @Override
    public List<ReturnRequestListResponseDto> findReturnRequestDtosByIsNull() {
        return queryFactory
                .select(new QReturnRequestListResponseDto(
                        returnRequest.returnId,
                        returnRequest.requestedAt,
                        returnRequest.returnedAt,
                        returnRequest.returnReason,
                        returnRequest.status,
                        returnRequest.order.orderId))
                .from(returnRequest)
                .where(returnRequest.status.eq(ReturnStatus.Requested))
                .fetch();
    }

    @Override
    public Optional<ReturnRequestResponseDto> findByReturnRequestId(Long returnRequestId) {

        ReturnRequestResponseDto result = queryFactory
                .select(new QReturnRequestResponseDto(
                        returnRequest.requestedAt,
                        returnRequest.returnedAt,
                        returnRequest.shippingFeeDeducted,
                        returnRequest.returnReason,
                        returnRequest.status,
                        returnRequest.order.orderId))
                .from(returnRequest)
                .where(returnRequest.returnId.eq(returnRequestId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<ReturnRequestListResponseDto> findByMemberId(Long memberId) {
        QOrder order = QOrder.order;

        return queryFactory
                .select(new QReturnRequestListResponseDto(
                        returnRequest.returnId,
                        returnRequest.requestedAt,
                        returnRequest.returnedAt,
                        returnRequest.returnReason,
                        returnRequest.status,
                        returnRequest.order.orderId))
                .from(returnRequest)
                .where(returnRequest.order.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public void updateStatusByReturnRequestId(Long returnRequestId, AdminReturnRequestRegisterDto adminReturnRequestRegisterDto) {
        queryFactory.update(returnRequest)
                .set(returnRequest.status, adminReturnRequestRegisterDto.getReturnStatus())
                .set(returnRequest.returnedAt, LocalDateTime.now())
                .where(returnRequest.returnId.eq(returnRequestId))
                .execute();
    }

}
