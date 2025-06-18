package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.QOrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.QOrder;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

// todo #1-2 주문 Querydsl Repository
// custom repository 생성 시에는 JPA 레포지토리를 상속 받은 클래스명에 "Impl"을 postfix 로 붙여야 함
@Repository
public class OrderRepositoryImpl extends QuerydslRepositorySupport implements OrderQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public OrderRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Order.class);
        this.queryFactory = queryFactory;
    }


    @Override
    public List<OrderListResponseDto> findOrderItemDtos() {
        QOrder order = QOrder.order;

        return queryFactory
                .select(new QOrderListResponseDto(
                        order.orderId,
                        order.orderDate,
                        order.totalPrice,
                        order.orderStatus))
                .from(order)
                .fetch();
    }



}
