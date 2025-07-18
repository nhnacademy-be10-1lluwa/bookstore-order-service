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
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface OrderService {

    // 전체 주문 내역 조회(ADMIN)
    Page<OrderListResponseDto> getAllOrders(Pageable pageable);

    // orderId로 주문 내역 조회(ADMIN)
    OrderResponseDto getOrderByOrderId(Long orderId);

    Order getOrderEntityByOrderId(Long orderId);

    // 배달 날짜 변경
    void updateOrderDeliveryDate(Long orderId, LocalDate localDate);

    // memberId, orderId로 주문 내역 조회(MEMBERS)
    OrderResponseDto getOrderByMemberIdAndOrderId(Long memberId, Long orderId);

    // number 로 주문 내역 조회(ADMIN, MEMBERS)
    OrderResponseDto getOrderByNumber(String orderNumber);

    Page<OrderListResponseDto> getOrdersByMemberId(Long memberId, Pageable pageable);

    // 비회원 주문 조회
    OrderResponseDto getOrderByNumberAndContact(String orderNumber, String recipientContact);

    // member 별 주문 내역 조회 (ADMIN, MEMBERS)
    Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable);

    // 주문 상태별 조회 (ADMIN)
    Page<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status, Pageable pageable);

    // member 주문하기 (cart)
    Order memberCreateOrderFromCartWithItems(Long memberId, MemberOrderRequest request);

    // member 주문하기 (direct)
    Order memberCreateOrderDirectWithItems(Long memberId, MemberOrderRequestDirect request);

    // guest 주문하기 (direct)
    Order guestCreateOrderDirectWithItems(GuestOrderRequestDirect request);

    // orderNumber로 주문 취소하기(MEMBERS, GUEST) - 재고 수량 상승, -> 현재 final_price = (단가(price) + 포장비(package_fee)) * 수량(quantity) - 할인(discount_price) - 포인트(used_point) + 배송비(shipping_fee)
    OrderResponseDto cancelOrderByOrderNumber(String orderNumber);

   // id로 주문 환불하기(MEMBERS, GUEST)
    OrderResponseDto refundOrderById(Long orderId, ReturnRequestCreateRequestDto dto);

    // id로 주문 상태 변경하기(ADMIN)
    void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto);

    // number로 주문 상태 변경하기 (ADMIN)
    void updateOrderStatusByOrderNumber(String orderNumber, OrderUpdateStatusDto orderUpdateDto);

    // 결제 완료
    void updateOrderPaymentByOrderNumber(String orderNumber);

    // 주문 초기 데이터 조회(member, 장바구니용)
    MemberOrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId);

    // 주문 초기 데이터 조회(Guest, 장바구니용)
    GuestOrderInitFromCartResponseDto getGuestOrderInitFromCartData(Long cartId);

    // 주문 초기 게이터 조회(member, 바로 구매용)
    MemberOrderInitDirectResponseDto getOrderInitDirectData(Long bookId, Long memberId);

    // 주문 초기 데이터 조회(guest, 바로 구매용)
    GuestOrderInitDirectResponseDto getGuestOrderInitDirectData(Long bookId);

    // 주문 도서에 대한 구매 확정여부를 검사
    boolean isConfirmedOrder(Long memberId, Long bookId);

}