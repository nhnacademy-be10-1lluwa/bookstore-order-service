package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/order/admin")
public class AdminOrderController {

    private final OrderService orderService;

    // 전체 주문내역 확인
    @GetMapping(value = "/orders", params = "!status")
    public ResponseEntity<Page<OrderListResponseDto>> getOrderAll(Pageable pageable) {
        Page<OrderListResponseDto> response = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 상태별 주문내역 확인
    @GetMapping(value = "/orders", params = "status")
    public ResponseEntity<Page<OrderListResponseDto>> getOrdersByStatus(@RequestParam("status") OrderStatus status, Pageable pageable) {
        Page<OrderListResponseDto> response = orderService.getOrderByOrderStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    // 멤버별 주문내역 조회
    @GetMapping(value = "/orders", params = "memberId")
    public ResponseEntity<Page<OrderListResponseDto>> getOrdersByMemberId(@RequestParam("memberId") Long memberId, Pageable pageable) {
        Page<OrderListResponseDto> response = orderService.getOrderByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 내역 단건조회
    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable String orderNumber) {
        OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경
    @PutMapping("/orders/{orderNumber}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderNumber, @RequestBody OrderUpdateStatusDto dto) {
        orderService.updateOrderStatusByOrderNumber(orderNumber, dto);
        return ResponseEntity.noContent()
                .location(URI.create("/order/admin/orders/" + orderNumber))
                .build();
    }
}
