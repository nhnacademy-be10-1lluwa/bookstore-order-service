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
    @GetMapping("/orders/{order-id}")
    public ResponseEntity<OrderResponseDto> getOrderDetail(@PathVariable("order-id") Long orderId) {
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
    @GetMapping(value = "/orders", params = "member-id")
    public ResponseEntity<Page<OrderListResponseDto>> getOrdersByMemberId(@RequestParam("member-id") Long memberId, Pageable pageable) {
        Page<OrderListResponseDto> response = adminOrderService.getOrderByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 내역 단건조회
    @GetMapping("/orders/by-number/{order-number}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@PathVariable("order-number") String orderNumber) {
        OrderResponseDto response = adminOrderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경 orderId
    @PutMapping("/orders/{order-id}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("order-id") Long orderId, @RequestBody OrderUpdateStatusDto dto) {
        if (dto.getOrderStatus() == OrderStatus.Shipped) {
            adminOrderService.updateOrderDeliveryDate(orderId, LocalDate.now());
        }
        commonOrderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.noContent().build();
    }
}
