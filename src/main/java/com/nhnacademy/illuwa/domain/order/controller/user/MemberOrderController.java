package com.nhnacademy.illuwa.domain.order.controller.user;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookDto;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.service.OrderItemService;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/member")
public class MemberOrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final ProductApiClient productApiClient;


    @GetMapping(value = "/init-from-cart")
    public ResponseEntity<MemberOrderInitFromCartResponseDto> getOrderInitFromCart(@RequestHeader("X-USER-ID") Long memberId) {
        MemberOrderInitFromCartResponseDto response = orderService.getOrderInitFromCartData(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/init-member-info/books/{bookId}")
    public ResponseEntity<MemberOrderInitDirectResponseDto> getOrderInitDirect(@RequestHeader("X-USER-ID") Long memberId, @PathVariable("bookId") Long bookId) {
        MemberOrderInitDirectResponseDto response = orderService.getOrderInitDirectData(bookId, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@RequestHeader("X-USER-ID") Long memberId, @PathVariable("orderId") Long orderId) {
        OrderResponseDto response = orderService.getOrderByMemberIdAndOrderId(memberId, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/history")
    public ResponseEntity<Map<String, Object>> getNewOrdersHistory(@RequestHeader("X-USER-ID") Long memberId,
                                                                @PageableDefault(size = 10, sort = "orderDate") Pageable pageable) {
        Page<OrderListResponseDto> page = orderService.getOrdersByMemberId(memberId, pageable);
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


    @PostMapping("/submit")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequest(@RequestHeader("X-USER-ID") Long memberId, @RequestBody @Valid MemberOrderRequest memberOrderRequest) {
        Order order = orderService.memberCreateOrderFromCartWithItems(memberId, memberOrderRequest);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);

        // 책 제목 조회 및 설정
        setTitle(response.getItems());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit-direct")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequestDirect(@RequestHeader("X-USER-ID") Long memberId, @RequestBody @Valid MemberOrderRequestDirect memberOrderRequestDirect) {
        Order order = orderService.memberCreateOrderDirectWithItems(memberId, memberOrderRequestDirect);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        // 책 제목 조회 및 설정
        setTitle(response.getItems());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{orderNumber}/orderItems/{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemByOrderItemId(@PathVariable String orderNumber, @PathVariable Long orderItemId) {
        OrderItemResponseDto response = orderItemService.getOrderItemById(orderItemId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/confirmed")
    public ResponseEntity<Boolean> isConfirmedOrder(@RequestHeader("X-USER-ID") Long memberId, @RequestParam Long bookId) {
        return ResponseEntity.ok(orderService.isConfirmedOrder(memberId, bookId));
    }


    /*ㅡㅡㅡㅡㅡ 헬퍼 ㅡㅡㅡㅡㅡㅡ*/

    private void setTitle(List<OrderItemResponseDto> items) {
        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }
    }
}
