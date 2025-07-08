package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    // 전체 주문 내역 조회(ADMIN)
    Page<OrderListResponseDto> getAllOrders(Pageable pageable);

    // id로 주문 내역 조회(ADMIN, MEMBERS)
    OrderResponseDto getOrderById(Long orderId);

    // number 로 주문 내역 조회(ADMIN, MEMBERS)
    OrderResponseDto getOrderByNumber(String orderNumber);


    OrderResponseDto getOrderByNumberAndContact(String orderNumber, String recipientContact);

    // member 별 주문 내역 조회 (ADMIN, MEMBERS)
    Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable);

    // 주문 상태별 조회 (ADMIN)
    Page<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status, Pageable pageable);

    // member 주문하기 (cart)
    Order memberCreateOrderFromCartWithItems(Long memberId, MemberOrderRequest request);

    // member 주문하기 (direct)
    Order memberCreateOrderDirectWithItems(Long memberId, MemberOrderRequestDirect request);

    // guest 주문하기 (cart)
    Order guestCreateOrderFromCartWithItems(Long memberId, GuestOrderRequest request);

    // guest 주문하기 (direct)
    Order guestCreateOrderDirectWithItems(Long memberId, GuestOrderRequestDirect request);


    // id로 주문 취소하기(MEMBERS)
    void cancelOrderById(Long orderId);

    // number로 주문 취소하기(MEMBERS)
    void cancelOrderByOrderNumber(String orderNumber);

    // id로 주문 상태 변경하기(ADMIN)
    void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto);

    // number로 주문 상태 변경하기 (ADMIN)
    void updateOrderStatusByOrderNumber(String orderNumber, OrderUpdateStatusDto orderUpdateDto);

    // 주문 초기 데이터 조회(member, 장바구니용)
    MemberOrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId);

    // 주문 초기 데이터 조회(Guest, 장바구니용)
    GuestOrderInitFromCartResponseDto getGuestOrderInitFromCartData(Long cartId);

    // 주문 초기 게이터 조회(member, 바로 구매용)
    MemberOrderInitDirectResponseDto getOrderInitDirectData(Long bookId, Long memberId);

    // 주문 초기 데이터 조회(guest, 바로 구매용)
    GuestOrderInitDirectResponseDto getGuestOrderInitDirectData(Long bookId, Long memberId);
}