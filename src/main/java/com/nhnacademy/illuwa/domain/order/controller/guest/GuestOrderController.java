package com.nhnacademy.illuwa.domain.order.controller.guest;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookDto;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminOrderService;
import com.nhnacademy.illuwa.domain.order.service.guest.GuestOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order/guests")
@Tag(name = "비회원 API", description = "비회원용 주문 API")
public class GuestOrderController {

    private final GuestOrderService guestOrderService;
    private final AdminOrderService adminOrderService;
    private final ProductApiClient productApiClient;

    // 비회원 바로 주문하기 내용 조회
    @Operation(summary = "비회원 주문 내용 조회", description = "비회원이 주문하는데 필요한 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패"),
            @ApiResponse(responseCode = "409", description = "도서 수량 부족")
    })
    @GetMapping("/orders/init/books/{book-id}")
    public ResponseEntity<GuestOrderInitDirectResponseDto> getOrderInitDirect(
            @Parameter(name = "bookId", description = "도서 ID", required = true)
            @PathVariable("book-id") Long bookId) {
        GuestOrderInitDirectResponseDto response = guestOrderService.getGuestOrderInitDirectData(bookId);
        return ResponseEntity.ok(response);
    }

    // 비회원 바로 구매
    @Operation(summary = "비회원 바로 주문하기", description = "비회원이 주문 요청한 내용을 즉시 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "주문 도서 정보 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/orders/direct")
    public ResponseEntity<OrderCreateResponseDto> guestOrderRequestDirect(@RequestBody @Valid GuestOrderRequestDirect request) {
        Order order = guestOrderService.guestCreateOrderDirectWithItems(request);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);

        // 책 제목 조회 및 설정
        for (OrderItemResponseDto item : response.getItems()) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }
        return ResponseEntity.ok(response);
    }

    // 비회원 주문조회
    @Operation(summary = "비회원 주문조회", description = "비회원이 주문내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @GetMapping("/orders/{order-id}")
    public ResponseEntity<OrderResponseDto> getOrderHistory(@PathVariable("order-id") Long orderId) {
        OrderResponseDto response = adminOrderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

}
