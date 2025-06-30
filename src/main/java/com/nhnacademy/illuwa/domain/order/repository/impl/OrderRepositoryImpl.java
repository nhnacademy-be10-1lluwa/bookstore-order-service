package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.QOrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.QOrderResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.QOrder;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderQuerydslRepository;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


// custom repository 생성 시에는 JPA 레포지토리를 상속 받은 클래스명에 "Impl"을 postfix 로 붙여야 함
@Repository
public class OrderRepositoryImpl extends QuerydslRepositorySupport implements OrderQuerydslRepository {

    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;

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
                        order.orderDate,
                        order.totalPrice,
                        order.orderStatus
                ))
                .from(order)
                .orderBy(order.orderDate.desc());

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
    public Optional<OrderResponseDto> findOrderDto(Long orderId) {

        OrderResponseDto result =  queryFactory
                .select(new QOrderResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.memberId,
                        order.orderDate,
                        order.deliveryDate,
                        order.totalPrice,
                        order.orderStatus))
                .from(order)
                .where(order.orderId.eq(orderId))
                .fetchOne();

        return Optional.ofNullable(result);
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
    public Optional<OrderResponseDto> findOrderDtoByOrderNumber(String orderNumber) {
        OrderResponseDto result =  queryFactory
                .select(new QOrderResponseDto(
                        order.orderId,
                        order.orderNumber,
                        order.memberId,
                        order.orderDate,
                        order.deliveryDate,
                        order.totalPrice,
                        order.orderStatus))
                .from(order)
                .where(order.orderNumber.eq(orderNumber))
                .fetchOne();

        return Optional.ofNullable(result);
    }

}
