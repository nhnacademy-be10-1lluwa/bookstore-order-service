package com.nhnacademy.illuwa.domain.order.controller.guest;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitFromCartResponseDto;
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
@RequestMapping("/api/order/guest")
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

    @GetMapping("/order-history/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderHistory(@PathVariable("orderId") Long orderId) {
        OrderResponseDto response = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/init-guest-info/books/{book-id}")
    public ResponseEntity<GuestOrderInitDirectResponseDto> getOrderInitDirect(@PathVariable("book-id") Long bookId) {
        GuestOrderInitDirectResponseDto response = orderService.getGuestOrderInitDirectData(bookId);
        return ResponseEntity.ok(response);
    }

    // 비회원 바로 구매
    @PostMapping("/submit-direct")
    public ResponseEntity<OrderCreateResponseDto> guestOrderRequestDirect(@RequestBody @Valid GuestOrderRequestDirect request) {
        Order order = orderService.guestCreateOrderDirectWithItems(request);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(response);
    }
}
