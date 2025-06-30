package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberGradeUpdateRequest;

import java.util.List;
import java.util.Optional;


// todo #1-3 Querydsl 인터페이스 생성
public interface OrderQuerydslRepository {
    // 모든 주문 가져오기
    List<OrderListResponseDto> findOrderDtos();

    // 주문 아이디로 주문 찾기
    Optional<OrderResponseDto> findOrderDto(Long orderId);

    // 멤버별 3개원 간 순수 주문 금액 조회
    List<MemberGradeUpdateRequest> findAllGradeDto();
}
