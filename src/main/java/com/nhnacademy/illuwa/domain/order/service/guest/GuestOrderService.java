package com.nhnacademy.illuwa.domain.order.service.guest;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;

public interface GuestOrderService {

    // guest 주문하기 (direct)
    Order guestCreateOrderDirectWithItems(GuestOrderRequestDirect request);

    // 주문 초기 데이터 조회(guest, 바로 구매용)
    GuestOrderInitDirectResponseDto getGuestOrderInitDirectData(Long bookId);
}
