package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.QOrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.QOrderItem;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderItemQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderItemRepositoryImpl extends QuerydslRepositorySupport implements OrderItemQuerydslRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    private final QOrderItem orderItem = QOrderItem.orderItem;

    public OrderItemRepositoryImpl(JPAQueryFactory queryFactory) {
        super(OrderItem.class);

    }

    @Override
    public List<OrderItemResponseDto> findAllOrderItemDtos() {
        return queryFactory
                .select(new QOrderItemResponseDto(
                        orderItem.orderItemId,
                        orderItem.bookId,
                        orderItem.quantity,
                        orderItem.price,
                        orderItem.packaging.packagingId))
                .from(orderItem)
                .fetch();
    }

    @Override
    public Optional<OrderItemResponseDto> findOrderItemDtoByOrderItemId(Long orderItemId) {

        OrderItemResponseDto result = queryFactory
                .select(new QOrderItemResponseDto(
                        orderItem.orderItemId,
                        orderItem.bookId,
                        orderItem.quantity,
                        orderItem.price,
                        orderItem.packaging.packagingId))
                .from(orderItem)
                .where(orderItem.orderItemId.eq(orderItemId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<OrderItemResponseDto> findOrderItemDtosByOrderId(Long orderId) {
        return queryFactory
                .select(new QOrderItemResponseDto(
                        orderItem.orderItemId,
                        orderItem.bookId,
                        orderItem.quantity,
                        orderItem.price,
                        orderItem.packaging.packagingId))
                .from(orderItem)
                .where(orderItem.order.orderId.eq(orderId))
                .fetch();
    }

}
