package com.nhnacademy.illuwa.domain.order.controller.common;


import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/common")
public class CommonOrderController {

    private final OrderService orderService;

    @PostMapping("/payment-success/{orderNumber}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("orderNumber") String orderNumber) {
        orderService.updateOrderPaymentByOrderNumber(orderNumber);
        return ResponseEntity.ok().build();
    }

    // 주문 취소 - 배송 전
    @GetMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> orderCancel(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrderById(orderId);
        return ResponseEntity.noContent().build();
    }

    // 주문 환불 - 배송 후
    @PutMapping("/refund/{orderId}")
    public ResponseEntity<Void> guestOrderRequestRefund(@PathVariable Long orderId, ReturnRequestCreateRequestDto dto) {
        orderService.refundOrderById(orderId, dto);
        return ResponseEntity.noContent().build();
    }
}
