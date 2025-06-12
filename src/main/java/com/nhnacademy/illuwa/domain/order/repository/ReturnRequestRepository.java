package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    // 반품비 정책 전체 조회
    // findAll

    // 반품비 정책 단일 조회
    ReturnRequest findByReturnId(Long returnRequestId);
}
