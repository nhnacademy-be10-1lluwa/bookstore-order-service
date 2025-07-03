package com.nhnacademy.illuwa.domain.order.controller.guest;

import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/order/guest")
public class GuestOrderController {

    private final OrderService orderService;

    @GetMapping("/init-from-cart")
    public ResponseEntity<GuestOrderInitFromCartResponseDto> getOrderInitFromCart(
            @CookieValue(value = "cartId", required = false) Long cartId
    ) {
        if (cartId == null) {
            throw new IllegalArgumentException("장바구니 정보가 없습니다.");
        }
        GuestOrderInitFromCartResponseDto response = orderService.getGuestOrderInitFromCartData(cartId);
        return ResponseEntity.ok(response);
    }

    // 비회원 장바구니 주문
    @PostMapping("/submit")
    public ResponseEntity<OrderCreateResponseDto> guestOrderRequest(@RequestBody GuestOrderRequest guestOrderRequest) {
        Order order = orderService.guestCreateOrderFromCartWithItems(guestOrderRequest);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(response);
    }



    // 비회원 주문내역 확인
    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String orderNumber) {
        OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

}
