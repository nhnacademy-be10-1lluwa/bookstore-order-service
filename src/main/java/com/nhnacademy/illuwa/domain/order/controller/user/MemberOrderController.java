package com.nhnacademy.illuwa.domain.order.controller.user;

import com.nhnacademy.illuwa.common.annotation.CurrentUserId;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderCartFactory;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.service.OrderItemService;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order/member")
public class MemberOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping(value = "/init-from-cart")
    public ResponseEntity<MemberOrderInitFromCartResponseDto> getOrderInitFromCart(@CurrentUserId Long memberId) {
        MemberOrderInitFromCartResponseDto response = orderService.getOrderInitFromCartData(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/init-member-info/books/{book-id}")
    public ResponseEntity<MemberOrderInitDirectResponseDto> getOrderInitDirect(@CurrentUserId Long memberId, @PathVariable("book-id") Long bookId) {
        MemberOrderInitDirectResponseDto response = orderService.getOrderInitDirectData(bookId, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/orders/{order-number}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable("order-number") String orderNumber) {
        OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/history")
    public ResponseEntity<Page<OrderListResponseDto>> getOrdersHistory(@CurrentUserId Long memberId, Pageable pageable) {
        Page<OrderListResponseDto> response = orderService.getOrderByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequest(@CurrentUserId Long memberId, @RequestBody @Valid MemberOrderRequest memberOrderRequest) {
        /*Order order = orderService.memberCreateOrderFromCartWithItems(memberId, memberOrderRequest);*/
        Order order = orderService.memberCreateOrderFromCartWithItems(memberId, memberOrderRequest);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-direct")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequestDirect(@CurrentUserId Long memberId, @RequestBody @Valid MemberOrderRequestDirect memberOrderRequestDirect) {
        Order order = orderService.memberCreateOrderDirectWithItems(memberId, memberOrderRequestDirect);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/orders/{orderNumber}/orderItems/{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemByOrderItemId(@PathVariable String orderNumber, @PathVariable Long orderItemId) {
        OrderItemResponseDto response = orderItemService.getOrderItemById(orderItemId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/orders/{orderNumber}/cancel")
    public ResponseEntity<Void> orderCancel(@PathVariable String orderNumber) {
        orderService.cancelOrderByOrderNumber(orderNumber);
        return ResponseEntity.noContent().build();
    }
}
