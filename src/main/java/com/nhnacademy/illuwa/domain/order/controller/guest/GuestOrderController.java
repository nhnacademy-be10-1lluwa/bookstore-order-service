package com.nhnacademy.illuwa.domain.order.controller.guest;

import com.nhnacademy.illuwa.common.annotation.CurrentUserId;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import jakarta.validation.Valid;
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
            throw new NotFoundException("장바구니 정보가 없습니다.");
        }
        GuestOrderInitFromCartResponseDto response = orderService.getGuestOrderInitFromCartData(cartId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order-history/{order-number}")
    public ResponseEntity<OrderResponseDto> getOrderHistory(@PathVariable("order-number") String orderNumber, @RequestParam("contact") String recipientContact) {
        OrderResponseDto response = orderService.getOrderByNumberAndContact(orderNumber, recipientContact);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/init-member-info/books/{book-id}")
    public ResponseEntity<GuestOrderInitDirectResponseDto> getOrderInitDirect(@PathVariable("book-id") Long bookId, @CurrentUserId Long memberId) {
        GuestOrderInitDirectResponseDto response = orderService.getGuestOrderInitDirectData(bookId, memberId);
        return ResponseEntity.ok(response);
    }

    // 비회원 장바구니 주문
    @PostMapping("/submit")
    public ResponseEntity<OrderCreateResponseDto> guestOrderRequest(@RequestBody @Valid GuestOrderRequest guestOrderRequest) {
        /*Order order = orderService.guestCreateOrderFromCartWithItems(guestOrderRequest);*/
        Order order = orderService.guestCreateOrderFromCartWithItems(null, guestOrderRequest);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(response);
    }

    // 비회원 바로 구매
    @PostMapping("/submit-direct")
    public ResponseEntity<OrderCreateResponseDto> guestOrderRequestDirect(@RequestBody @Valid GuestOrderRequestDirect guestOrderRequestDirect) {
        /*Order order = orderService.guestCreateOrderDirectWithItems(guestOrderRequestDirect);*/
        Order order = orderService.guestCreateOrderDirectWithItems(null, guestOrderRequestDirect);
        OrderCreateResponseDto responseDto = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(responseDto);
    }


    // 비회원 주문내역 확인
    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String orderNumber) {
        OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

}
