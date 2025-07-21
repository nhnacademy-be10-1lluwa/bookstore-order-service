package com.nhnacademy.illuwa.domain.order.service.member;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberOrderService {

    // memberId로 주문 내역들 조회(MEMBERS)
    Page<OrderListResponseDto> getOrdersByMemberId(Long memberId, Pageable pageable);

    // memberId, orderId로 주문 내역 조회(MEMBERS)
    OrderResponseDto getOrderByMemberIdAndOrderId(Long memberId, Long orderId);

    // member 주문하기 (cart)
    Order memberCreateOrderFromCartWithItems(Long memberId, MemberOrderRequest request);

    // member 주문하기 (direct)
    Order memberCreateOrderDirectWithItems(Long memberId, MemberOrderRequestDirect request);

    // 주문 초기 데이터 조회(member, 장바구니용)
    MemberOrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId);

    // 주문 초기 게이터 조회(member, 바로 구매용)
    MemberOrderInitDirectResponseDto getOrderInitDirectData(Long bookId, Long memberId);

    // 주문 도서에 대한 구매 확정여부를 검사
    boolean isConfirmedOrder(Long memberId, Long bookId);
}
