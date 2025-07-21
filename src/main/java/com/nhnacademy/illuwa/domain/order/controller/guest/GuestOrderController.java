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
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/api/order/guest")
public class GuestOrderController {

    private final GuestOrderService guestOrderService;
    private final AdminOrderService adminOrderService;
    private final ProductApiClient productApiClient;

    @GetMapping("/order-history/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderHistory(@PathVariable("orderId") Long orderId) {
        OrderResponseDto response = adminOrderService.getOrderByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/init-guest-info/books/{book-id}")
    public ResponseEntity<GuestOrderInitDirectResponseDto> getOrderInitDirect(@PathVariable("book-id") Long bookId) {
        GuestOrderInitDirectResponseDto response = guestOrderService.getGuestOrderInitDirectData(bookId);
        return ResponseEntity.ok(response);
    }

    // 비회원 바로 구매
    @PostMapping("/submit-direct")
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
}
