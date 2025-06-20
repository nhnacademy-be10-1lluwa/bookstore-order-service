package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import com.nhnacademy.illuwa.domain.order.repository.custom.ReturnRequestQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long>, ReturnRequestQuerydslRepository {

    // 반품 전체 조회
    // findAll

    // 처리 대기 중인 반품 요청 전체 조회
    List<ReturnRequest> findByReturnedAtIsNull();

    // 반품 ID 로 단일 조회 (ADMIN)
    Optional<ReturnRequest> findByReturnId(Long returnRequestId);

    // 반품 요청 취소하기(MEMBERS)
    int removeReturnRequestByReturnId(Long returnId);
}

