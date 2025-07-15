package com.nhnacademy.illuwa.domain.order.controller.common;


import com.nhnacademy.illuwa.domain.order.service.OrderService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
