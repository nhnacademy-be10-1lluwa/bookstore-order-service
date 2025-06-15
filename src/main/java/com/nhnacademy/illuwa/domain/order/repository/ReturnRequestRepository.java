package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    // 반품 전체 조회
    // findAll

    // 처리 대기 중인 반품 요청 전체 조회
    List<ReturnRequest> findByReturnedAtIsNull();

    // 반품 ID 로 단일 조회 (ADMIN)
    Optional<ReturnRequest> findByReturnId(Long returnRequestId);

    // 유저별 반품 내역 조회 (MEMBERS)
    List<ReturnRequest> findByMemberId(String memberId);

    // 반품 요청 취소하기(MEMBERS)
    int removeReturnRequestByReturnId(long returnId);
//    select m.member_id, m.name from return_request as rr inner join orders as o on rr.order_order_id = o.order_id inner join members as m on o.member_id = m.member_id;
}

