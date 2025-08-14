package com.nhnacademy.illuwa.domain.order.controller.common;


import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/common")
@Tag(name = "공통 주문 API", description = "공통 주문 관련 API")
public class CommonOrderController {

    private final CommonOrderService commonOrderService;

    private String mode = "sync";

    // 주문 확정 - 주문 확정 시, 환불 / 결제 취소 불가
    @Operation(summary = "주문 확정", description = "사용자가 해당 주문의 상태를 주문 확정으로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "주문 확정 성공"),
            @ApiResponse(responseCode = "404", description = "주문 조회실패")
    })
    @PostMapping("/orders/{order-id}/confirm")
    public ResponseEntity<Void> updateOrderConfirmed(
            @Parameter(name = "orderId", description = "주문 ID", required = true)
            @PathVariable("order-id") Long orderId) {
        OrderUpdateStatusDto dto = new OrderUpdateStatusDto(OrderStatus.Confirmed);
        commonOrderService.updateOrderStatus(orderId, dto);
        return ResponseEntity.noContent().build();
    }

    // 주문 취소 - 결제 전 : 완료
    @Operation(summary = "주문 취소", description = "사용자가 해당 주문 내역을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "404", description = "주문 조회 실패")
    })
    @PostMapping("/orders/{order-id}/order-cancel")
    public ResponseEntity<Void> orderCancel(
            @Parameter(name = "orderId", description = "주문 ID", required = true)
            @PathVariable("order-id") Long orderId) {
        commonOrderService.orderCancel(orderId);
        return ResponseEntity.noContent().build();
    }

    // 결제 취소 - 배송 전 : 완료
    @Operation(summary = "결제 취소", description = "사용자가 해당 주문의 상태를 주문 취소로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "404", description = "주문 조회실패")
    })
    @PostMapping("/orders/{order-number}/payment-cancel")
    public ResponseEntity<Void> paymentCancel(
            @Parameter(name = "orderNumber", description = "주문 번호", required = true)
            @PathVariable("order-number") String orderNumber) {
        commonOrderService.cancelOrderByOrderNumber(orderNumber);
        return ResponseEntity.noContent().build();
    }

    // 주문 환불 - 배송 후 : 완료
    @Operation(summary = "주문 확정", description = "사용자가 주문의 상태를 환불로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "환불 성공"),
            @ApiResponse(responseCode = "404", description = "주문 조회실패")
    })
    @PostMapping("/orders/{order-id}/refund")
    public ResponseEntity<Void> guestOrderRequestRefund(
            @Parameter(name = "orderId", description = "주문 ID", required = true)
            @PathVariable("order-id") Long orderId, @Valid @RequestBody ReturnRequestCreateRequestDto dto) {
        commonOrderService.refundOrderById(orderId, dto);
        return ResponseEntity.noContent().build();
    }


    // 결제 완료 ( payment -> order )
    @Operation(summary = "주문 확정", description = "결제 성공한 주문의 상태를 배송 출고 준비 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "404", description = "주문 조회실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/payment-success/{order-number}")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(name = "orderNumber", description = "주문 번호", required = true)
            @PathVariable("order-number") String orderNumber){
        StopWatch sw = new StopWatch("payment-success-callback");
        sw.start("update-order-status");
        commonOrderService.updateOrderPaymentByOrderNumber(orderNumber);
        sw.stop();
        log.info("[/payment-success] total={}ms detail=\n{}", sw.getTotalTimeMillis(), sw.prettyPrint());
        return ResponseEntity.noContent().build();
    }
}
