package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


// todo #1-3 Querydsl 인터페이스 생성
public interface OrderQuerydslRepository {
    // 모든 주문 가져오기
    Page<OrderListResponseDto> findOrderDtos(Pageable pageable);

    // 주문 아이디로 주문 조회
    Optional<OrderResponseDto> findOrderDtoByOrderId(Long OrderId);

    Optional<Order> findOrderByOrderId(Long orderId);

    // 주문 넘버로 주문 조회
    Optional<Order> findOrderByOrderNumber(String orderNumber);

    // 주문/멤버 아이디로 주문 조회
    Optional<OrderResponseDto> findOrderDtoByMemberIdAndOrderId(Long memberId, Long orderId);

    // 주문 상태로 주문 찾기
    Page<OrderListResponseDto> findOrdersDtoByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    // 멤버별 3개원 간 순수 주문 금액 조회
    List<MemberGradeUpdateRequest> findAllGradeDto();

    // 멤버 주문 내역들 찾기
    Page<OrderListResponseDto> findOrderListDtoByMemberId(Long memberId, Pageable pageable);

    // 주문번호로 주문 찾기
    Optional<OrderResponseDto> findOrderDtoByOrderNumber(String orderNumber);

    // 비회원 주문 찾기
    Optional<OrderResponseDto> findOrderDtoByOrderNumberAndContact(String orderNumber, String recipientContact);

    // 멤버별 3개월 주문내역 조회
    List<MemberGradeUpdateRequest> buildMemberGradeUpdateRequest();

    // 주문한 책이 구매 확정상태인지 확인
    boolean existsConfirmedOrderByMemberIdAndBookId(Long memberId, Long bookId);

    // 주문 상태 변경하기
    void updateStatusByOrderId(Long orderId, OrderUpdateStatusDto dto);


    // 주문 상태 변경하기 (주문번호)
    void updateStatusByOrderNumber(String orderNumber);



    /*-----------  배송 전 결제 취소 ------------*/
    // 주문 상태 변경하기
    void updateStatusByOrderId(Long orderId, OrderStatus status);

    // 주문 배송 날짜 변경
    void updateDeliveryDateByOrderId(Long orderId, LocalDate localDate);

}
