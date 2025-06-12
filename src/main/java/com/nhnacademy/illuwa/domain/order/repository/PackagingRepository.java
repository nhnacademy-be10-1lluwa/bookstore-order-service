package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PackagingRepository extends JpaRepository<Packaging, Long> {

    // findAll 전체 포장 옵션

    // 해당 포장 옵션 조회
    Packaging findByPackagingId(Long packagingId);
}
