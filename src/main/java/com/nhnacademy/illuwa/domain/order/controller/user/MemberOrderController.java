package com.nhnacademy.illuwa.domain.order.controller.user;

import com.nhnacademy.illuwa.common.annotation.CurrentUserId;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.service.OrderItemService;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/order/member")
public class MemberOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    /*@GetMapping(value = "/orders")
    public ResponseEntity<Page<OrderListResponseDto>> getOrderByMemberId(@CurrentUserId Long memberId, Pageable pageable) {
        Page<OrderListResponseDto> response = orderService.getOrderByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }*/

    @GetMapping(value = "/init-from-cart")
    public ResponseEntity<MemberOrderInitFromCartResponseDto> getOrderInitFromCart(@RequestParam("memberId") Long memberId) {
        MemberOrderInitFromCartResponseDto response = orderService.getOrderInitFromCartData(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/orders/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable String orderNumber) {
        OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequest(@CurrentUserId Long memberId, @RequestBody MemberOrderRequest memberOrderRequest) {
        Order order = orderService.memberCreateOrderFromCartWithItems(memberId, memberOrderRequest);
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
