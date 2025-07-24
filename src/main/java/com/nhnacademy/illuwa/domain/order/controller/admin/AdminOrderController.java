package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminOrderService;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "어드민 API", description = "어드민용 API")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;
    private final CommonOrderService commonOrderService;

    // 전체 주문내역 확인
    @Operation(summary = "주문내역 전체 조회", description = "어드민이 사용자의 전체 주문내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping(value = "/orders", params = "!status")
    public ResponseEntity<Page<OrderListResponseDto>> getOrderAll(@ParameterObject Pageable pageable) {
        Page<OrderListResponseDto> response = adminOrderService.getAllOrders(pageable);
        return ResponseEntity.ok(response);
    }

    // orderId로 주문 상세 조회
    @Operation(summary = "order id 주문내역 상세 조회", description = "어드민이 사용자의 상세 주문내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 없음")
    })

    @GetMapping("/orders/{order-id}")
    public ResponseEntity<OrderResponseDto> getOrderDetail(@Parameter(name = "order-id", description = "주문 ID", required = true)
            @PathVariable("order-id") Long orderId) {
        OrderResponseDto response = adminOrderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    // 주문 상태별 주문내역 확인
    @Operation(summary = "주문 상태별 주문내역 조회", description = "어드민이 주문 상태별 주문내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping(value = "/orders", params = "status")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(
            @Parameter(description = "주문 상태", required = true)
            @RequestParam("status") OrderStatus status, @ParameterObject Pageable pageable) {
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
    @Operation(summary = "회원 별 주문내역 조회", description = "어드민이 회원별 주문내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping(value = "/orders", params = "member-id")
    public ResponseEntity<Page<OrderListResponseDto>> getOrdersByMemberId(
            @Parameter(description = "회원 ID", required = true)
            @RequestParam("member-id") Long memberId, @ParameterObject Pageable pageable) {
        Page<OrderListResponseDto> response = adminOrderService.getOrderByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    // 주문 내역 단건조회
    @Operation(summary = "order number 주문내역 상세 조회", description = "어드민이 사용자의 상세 주문내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @GetMapping("/orders/by-number/{order-number}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(
            @Parameter(description = "주문 번호", required = true)
            @PathVariable("order-number") String orderNumber) {
        OrderResponseDto response = adminOrderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경 orderId
    @Operation(summary = "주문 상태변경", description = "어드민이 사용자의 주문상태를 변경합니다.")
    @ApiResponse(responseCode = "204", description = "주문 상태 변경 성공")
    @PutMapping("/orders/{order-id}")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable("order-id") Long orderId,
            @Valid @RequestBody OrderUpdateStatusDto dto) {
        if (dto.getOrderStatus() == OrderStatus.Shipped) {
            adminOrderService.updateOrderDeliveryDate(orderId, LocalDate.now());
        }
        commonOrderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.noContent().build();
    }
}
