package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.AllShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.QAllShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.QShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.QShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.repository.custom.ShippingPolicyQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShippingPolicyRepositoryImpl extends QuerydslRepositorySupport implements ShippingPolicyQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    private final QShippingPolicy shippingPolicy = QShippingPolicy.shippingPolicy;

    @Autowired
    public ShippingPolicyRepositoryImpl(JPAQueryFactory queryFactory) {
        super(ShippingPolicy.class);
        this.queryFactory =queryFactory;
    }

    @Override
    public List<AllShippingPolicyDto> findAllShippingDtosPolicy() {
        return queryFactory
                .select(new QAllShippingPolicyDto(
                        shippingPolicy.shippingPolicyId,
                        shippingPolicy.minAmount,
                        shippingPolicy.fee,
                        shippingPolicy.active))
                .from(shippingPolicy)
                .fetch();
    }

    @Override
    public ShippingPolicyResponseDto findShippingPolicyDtoByActive(boolean active) {
        return queryFactory
                .select(new QShippingPolicyResponseDto(
                        shippingPolicy.shippingPolicyId,
                        shippingPolicy.minAmount,
                        shippingPolicy.fee))
                .from(shippingPolicy)
                .where(shippingPolicy.active.eq(active))
                .fetchOne();
    }

    @Override
    public Optional<ShippingPolicyResponseDto> findSHippingPolicyDtoByShippingPolicyId(long shippingPolicyId) {

        ShippingPolicyResponseDto result = queryFactory
                .select(new QShippingPolicyResponseDto(
                        shippingPolicy.shippingPolicyId,
                        shippingPolicy.minAmount,
                        shippingPolicy.fee))
                .from(shippingPolicy)
                .where(shippingPolicy.shippingPolicyId.eq(shippingPolicyId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
