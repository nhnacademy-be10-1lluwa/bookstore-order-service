package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminOrderService;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminUtilsService;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/order/admin")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final AdminUtilsService adminUtilsService;
    private final CommonOrderService commonOrderService;

    // 전체 주문내역 확인
    @GetMapping(value = "/orders", params = "!status")
    public ResponseEntity<Page<OrderListResponseDto>> getOrderAll(Pageable pageable) {
        Page<OrderListResponseDto> response = adminOrderService.getAllOrders(pageable);
        return ResponseEntity.ok(response);
    }

    // orderId로 주문 상세 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderDetail(@PathVariable("orderId") Long orderId) {
        OrderResponseDto response = adminOrderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    // 주문 상태별 주문내역 확인
    @GetMapping(value = "/orders", params = "status")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@RequestParam("status") OrderStatus status, Pageable pageable) {
        Page<OrderListResponseDto> page = adminOrderService.getOrderByOrderStatus(status, pageable);
        Map<String, Object> body = Map.of(
                "content", page.getContent(),
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "first", page.isFirst(),
                "last", page.isLast()
        );

        return ResponseEntity.ok(body);
    }

    // 멤버별 주문내역 조회
    @GetMapping(value = "/orders", params = "memberId")
    public ResponseEntity<Page<OrderListResponseDto>> getOrdersByMemberId(@RequestParam("memberId") Long memberId, Pageable pageable) {
        Page<OrderListResponseDto> response = adminOrderService.getOrderByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 내역 단건조회
    @GetMapping("/orders/by-number/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable String orderNumber) {
        OrderResponseDto response = adminOrderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경 orderId
    @PutMapping("/orders/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderUpdateStatusDto dto) {
        if (dto.getOrderStatus() == OrderStatus.Shipped) {
            adminOrderService.updateOrderDeliveryDate(orderId, LocalDate.now());
        }
        commonOrderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send-net-order-amount")
    public ResponseEntity<Void> triggerSchedulerManually() {
        int count = adminUtilsService.sendMonthlyNetOrderAmount();
        log.info("수동으로 월간 순주문 금액 전송 와료 - {}건", count);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-awaiting-orders")
    public ResponseEntity<Void> triggerSchedulerDeleteOrder() {
        Map<String, Integer> map = adminUtilsService.dbDataScheduler();
        log.info("수동으로 3일간 AwaitingPayment 상태의 주문 내역 삭제 - {}건 \n" +
                "삭제된 order item 개수 - {}개", map.get("deleteOrder"), map.get("deleteOrderItem"));
        return ResponseEntity.ok().build();
    }
}
