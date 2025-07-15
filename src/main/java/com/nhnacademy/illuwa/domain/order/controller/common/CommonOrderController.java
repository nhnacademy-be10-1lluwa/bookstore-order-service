package com.nhnacademy.illuwa.domain.order.controller.common;


import com.nhnacademy.illuwa.domain.order.service.MemberGradeService;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import com.nhnacademy.illuwa.domain.order.util.scheduler.MonthlyNetOrderAmountScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/common")
public class CommonOrderController {

    private final OrderService orderService;
    private final MonthlyNetOrderAmountScheduler scheduler;
    private final MemberGradeService memberGradeService;

    @PostMapping("/payment-success/{orderNumber}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("orderNumber") String orderNumber) {
        orderService.updateOrderPaymentByOrderNumber(orderNumber);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/send-net-order-amount")
    public ResponseEntity<Void> triggerSchedulerManually() {
        int count = memberGradeService.sendMonthlyNetOrderAmount();
        log.info("수동으로 원간 순주문 금액 전송 와료 - {}건", count);
        return ResponseEntity.ok().build();
    }
}
