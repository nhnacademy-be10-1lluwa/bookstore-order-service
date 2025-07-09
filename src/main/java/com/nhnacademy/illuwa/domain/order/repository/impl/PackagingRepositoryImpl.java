package com.nhnacademy.illuwa.domain.order.repository.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.QPackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.QPackaging;
import com.nhnacademy.illuwa.domain.order.repository.custom.PackagingQuerydslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PackagingRepositoryImpl extends QuerydslRepositorySupport implements PackagingQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    private final QPackaging packaging = QPackaging.packaging;

    @Autowired
    public PackagingRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Packaging.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<PackagingResponseDto> findPackagingDtos() {

        return queryFactory
                .select(new QPackagingResponseDto(
                        packaging.packagingId,
                        packaging.packagingName,
                        packaging.packagingPrice))
                .from(packaging)
                .fetch();
    }

    @Override
    public Optional<PackagingResponseDto> findPackagingDtoById(Long packagingId) {

        PackagingResponseDto result = queryFactory
                .select(new QPackagingResponseDto(
                        packaging.packagingId,
                        packaging.packagingName,
                        packaging.packagingPrice))
                .from(packaging)
                .where(packaging.packagingId.eq(packagingId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<PackagingResponseDto> findPackagingDtosByActive(boolean active) {

        return queryFactory
                .select(new QPackagingResponseDto(
                        packaging.packagingId,
                        packaging.packagingName,
                        packaging.packagingPrice))
                .from(packaging)
                .where(packaging.active.eq(active))
                .fetch();
    }
}
