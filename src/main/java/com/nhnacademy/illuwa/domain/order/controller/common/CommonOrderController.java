package com.nhnacademy.illuwa.domain.order.controller.common;


import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/common")
public class CommonOrderController {

    private final CommonOrderService commonOrderService;

    // 주문 확정 - 주문 확정 시, 환불 / 결제 취소 불가
    @PostMapping("/orders/{order-id}/confirm")
    public ResponseEntity<Void> updateOrderConfirmed(@PathVariable("order-id") Long orderId) {
        OrderUpdateStatusDto dto = new OrderUpdateStatusDto(OrderStatus.Confirmed);
        commonOrderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.noContent().build();
    }

    // 주문 취소 - 결제 전 : 완료
    @PostMapping("/orders/{order-id}/order-cancel")
    public ResponseEntity<Void> orderCancel(@PathVariable("order-id") Long orderId) {
        commonOrderService.orderCancel(orderId);
        return ResponseEntity.noContent().build();
    }

    // 결제 취소 - 배송 전 : 완료
    @PostMapping("/orders/{order-number}/payment-cancel")
    public ResponseEntity<Void> paymentCancel(@PathVariable("order-number") String orderNumber) {
        commonOrderService.cancelOrderByOrderNumber(orderNumber);
        return ResponseEntity.noContent().build();
    }

    // 주문 환불 - 배송 후 : 완료
    @PostMapping("/orders/{order-id}/refund")
    public ResponseEntity<Void> guestOrderRequestRefund(@PathVariable("order-id") Long orderId, @RequestBody ReturnRequestCreateRequestDto dto) {
        commonOrderService.refundOrderById(orderId, dto);
        return ResponseEntity.noContent().build();
    }


    // 결제 완료 ( payment -> order )
    @PostMapping("/payment-success/{order-number}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("order-number") String orderNumber) {
        commonOrderService.updateOrderPaymentByOrderNumber(orderNumber);
        return ResponseEntity.ok().build();
    }
}
