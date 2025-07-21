package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderQuerydslRepository;
import com.nhnacademy.illuwa.domain.order.entity.QOrder;
import com.nhnacademy.illuwa.domain.order.entity.QOrderItem;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


// custom repository 생성 시에는 JPA 레포지토리를 상속 받은 클래스명에 "Impl"을 postfix 로 붙여야 함

@Repository
public class OrderRepositoryImpl extends QuerydslRepositorySupport implements OrderQuerydslRepository {

    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;
    private final QOrderItem orderItem = QOrderItem.orderItem;

    @Autowired
    public OrderRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Order.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public Page<OrderListResponseDto> findOrderDtos(Pageable pageable) {

        JPAQuery<OrderListResponseDto> query = queryFactory
                .select(new QOrderListResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.orderDate,
                        order.deliveryDate,
                        order.totalPrice,
                        order.orderStatus
                ))
                .from(order)
                .orderBy(order.orderDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        List<OrderListResponseDto> contents = query.fetch();

        Long totalWrapper = queryFactory.select(order.count())
                .from(order)
                .fetchOne();

        long total = totalWrapper != null ? totalWrapper : 0L;

        return new PageImpl<>(contents,
                pageable,
                total);
    }

    @Override
    public Optional<OrderResponseDto> findOrderDtoByOrderId(Long orderId) {
        OrderResponseDto result = queryFactory
                .select(new QOrderResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.memberId,
                        order.orderDate,
                        order.deliveryDate,
                        order.shippingFee,
                        order.totalPrice,
                        order.orderStatus,
                        Expressions.nullExpression()))
                .from(order)
                .where(order.orderId.eq(orderId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Order> findOrderByOrderId(Long orderId) {
        return Optional.ofNullable(queryFactory.selectFrom(order)
                .leftJoin(order.items, orderItem).fetchJoin()
                .where(order.orderId.eq(orderId))
                .fetchOne());
    }

    @Override
    public Optional<Order> findOrderByOrderNumber(String orderNumber) {
        return Optional.ofNullable(queryFactory.selectFrom(order)
                .leftJoin(order.items, orderItem).fetchJoin()
                .where(order.orderNumber.eq(orderNumber))
                .fetchOne());
    }



    @Override
    public Optional<OrderResponseDto> findOrderDtoByMemberIdAndOrderId(Long memberId, Long orderId) {

        OrderResponseDto result = queryFactory
                .select(new QOrderResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.memberId,
                        order.orderDate,
                        order.deliveryDate,
                        order.shippingFee,
                        order.totalPrice,
                        order.orderStatus,
                        Expressions.nullExpression()))
                .from(order)
                .where(order.orderId.eq(orderId).and(order.memberId.eq(memberId)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<OrderListResponseDto> findOrdersDtoByOrderStatus(OrderStatus orderStatus, Pageable pageable) {

        JPAQuery<OrderListResponseDto> query = queryFactory
                .select(new QOrderListResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.orderDate,
                        order.deliveryDate,
                        order.totalPrice,
                        order.orderStatus
                ))
                .from(order)
                .where(order.orderStatus.eq(orderStatus))
                .orderBy(order.orderDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<OrderListResponseDto> content = query.fetch();

        Long totalWrapper = queryFactory.select(order.count())
                .from(order)
                .where(order.orderStatus.eq(orderStatus))
                .fetchOne();

        long total = totalWrapper != null ? totalWrapper : 0L;


        return new PageImpl<>(content,
                pageable,
                total);
    }

    @Override
    public List<MemberGradeUpdateRequest> findAllGradeDto() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        return queryFactory
                .from(order)
                .where(order.orderStatus.eq(OrderStatus.Confirmed).and(order.orderDate.after(threeMonthsAgo)))
                .transform(GroupBy.groupBy(order.memberId)
                        .list(Projections.constructor(
                                MemberGradeUpdateRequest.class,
                                order.memberId,
                                GroupBy.list(order.totalPrice)
                        )));



    }

    @Override
    public Page<OrderListResponseDto> findOrderListDtoByMemberId(Long memberId, Pageable pageable) {

        JPAQuery<OrderListResponseDto> query = queryFactory
                .select(new QOrderListResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.orderDate,
                        order.deliveryDate,
                        order.totalPrice,
                        order.orderStatus
                ))
                .from(order)
                .where(order.memberId.eq(memberId))
                .orderBy(order.orderDate.desc(), order.orderId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<OrderListResponseDto> contents = query.fetch();

        Long totalWrapper = queryFactory.select(order.count())
                .from(order)
                .where(order.memberId.eq(memberId))
                .fetchOne();

        long total = totalWrapper != null ? totalWrapper : 0L;

        return new PageImpl<>(contents,
                pageable,
                total);
    }

    @Override
    public Optional<OrderResponseDto> findOrderDtoByOrderNumber(String orderNumber) {
        OrderResponseDto result = queryFactory
                .select(new QOrderResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.memberId,
                        order.orderDate,
                        order.deliveryDate,
                        order.shippingFee,
                        order.totalPrice,
                        order.orderStatus,
                        Expressions.nullExpression()))
                .from(order)
                .where(order.orderNumber.eq(orderNumber))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<OrderResponseDto> findOrderDtoByOrderNumberAndContact(String orderNumber, String recipientContact) {
        OrderResponseDto result = queryFactory
                .select(new QOrderResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.memberId,
                        order.orderDate,
                        order.deliveryDate,
                        order.shippingFee,
                        order.totalPrice,
                        order.orderStatus,
                        Expressions.nullExpression()))
                .from(order)
                .where(order.orderNumber.eq(orderNumber).and(order.recipientContact.eq(recipientContact)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<MemberGradeUpdateRequest> buildMemberGradeUpdateRequest() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        return queryFactory
                .select(Projections.constructor(
                        MemberGradeUpdateRequest.class,
                        order.memberId,
                        order.totalPrice.sum().coalesce(Expressions.constant(BigDecimal.ZERO))
                ))
                .from(order)
                .where(order.orderStatus.eq(OrderStatus.Confirmed).and(order.orderDate.after(threeMonthsAgo)))
                .groupBy(order.memberId)
                .fetch();
    }

    @Override
    public boolean existsConfirmedOrderByMemberIdAndBookId(Long memberId, Long bookId) {

        return queryFactory
                .selectOne()
                .from(order)
                .join(order.items, orderItem)
                .where(
                        order.memberId.eq(memberId),
                        order.orderStatus.eq(OrderStatus.Confirmed),
                        orderItem.bookId.eq(bookId)
                )
                .fetchFirst() != null;
    }

    @Override
    public void updateStatusByOrderId(Long orderId, OrderUpdateStatusDto dto) {
        queryFactory.update(order)
                .set(order.orderStatus, dto.getOrderStatus())
                .set(order.deliveryDate, LocalDate.now())
                .where(order.orderId.eq(orderId))
                .execute();
    }

    @Override
    public void updateStatusByOrderId(Long orderId, OrderStatus status) {
        queryFactory.update(order)
                .set(order.orderStatus, status)
                .where(order.orderId.eq(orderId))
                .execute();
    }

    @Override
    public void updateDeliveryDateByOrderId(Long orderId, LocalDate localDate) {
        queryFactory.update(order)
                .set(order.deliveryDate, localDate)
                .where(order.orderId.eq(orderId))
                .execute();
    }

    @Override
    public long deleteByOrderId(Long orderId) {
        return queryFactory.delete(order)
                .where(order.orderId.eq(orderId))
                .execute();
    }

    @Override
    public void updateStatusByOrderNumber(String orderNumber) {
        queryFactory.update(order)
                .set(order.orderStatus, OrderStatus.Pending)
                .where(order.orderNumber.eq(orderNumber))
                .execute();
    }

}
