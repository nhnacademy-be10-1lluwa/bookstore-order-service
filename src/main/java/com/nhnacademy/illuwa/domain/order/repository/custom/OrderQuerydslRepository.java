package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;


// todo #1-3 Querydsl 인터페이스 생성
public interface OrderQuerydslRepository {
    // 모든 주문 가져오기
    Page<OrderListResponseDto> findOrderDtos(Pageable pageable);

    // 주문 아이디로 주문 찾기
    Optional<OrderResponseDto> findOrderDto(Long orderId);

    // 멤버별 3개원 간 순수 주문 금액 조회
    List<MemberGradeUpdateRequest> findAllGradeDto();


    // 주문번호로 주문 찾기
    Optional<OrderResponseDto> findOrderDtoByOrderNumber(String orderNumber);

    // 멤버별 3개월 주문내역 조회
    List<MemberGradeUpdateRequest> buildMemberGradeUpdateRequest();

    // 주문한 책이 구매 확정상태인지 확인
    boolean existsConfirmedOrderByMemberIdAndBookId(Long memberId, Long bookId);
}
