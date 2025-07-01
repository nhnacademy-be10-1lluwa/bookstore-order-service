package com.nhnacademy.illuwa.domain.order.controller.guest;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/order/guest")
public class GuestOrderController {

    private final OrderService orderService;

    // 비회원 주문내역 확인
    @GetMapping("/orders/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String orderNumber) {
        OrderResponseDto response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    // 비회원 주문하기
    @PostMapping("/orders")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequest(@RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        // todo guestId 넘기는 로직 만드릭
        Order order = orderService.createOrderWithItems(orderCreateRequestDto);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        return ResponseEntity.ok(response);
    }
}
