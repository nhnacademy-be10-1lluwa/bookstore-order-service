package com.nhnacademy.illuwa.domain.order.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommonOrderControllerTest {

    @InjectMocks
    private CommonOrderController controller;

    @Mock
    private CommonOrderService commonOrderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/order/common/orders/{order-id}/confirm - 주문 확정 (204)")
    void updateOrderConfirmed_success() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(post("/api/order/common/orders/{order-id}/confirm", orderId))
               .andExpect(status().isNoContent());

        ArgumentCaptor<OrderUpdateStatusDto> captor = ArgumentCaptor.forClass(OrderUpdateStatusDto.class);
        Mockito.verify(commonOrderService, times(1))
               .updateOrderStatus(eq(orderId), captor.capture());
        assertEquals(OrderStatus.Confirmed, captor.getValue().getOrderStatus());
    }

    @Test
    @DisplayName("POST /api/order/common/orders/{order-id}/order-cancel - 주문 취소 (204)")
    void orderCancel_success() throws Exception {
        Long orderId = 2L;

        mockMvc.perform(post("/api/order/common/orders/{order-id}/order-cancel", orderId))
               .andExpect(status().isNoContent());

        Mockito.verify(commonOrderService, times(1)).orderCancel(eq(orderId));
    }

    @Test
    @DisplayName("POST /api/order/common/orders/{order-number}/payment-cancel - 결제 취소 (204)")
    void paymentCancel_success() throws Exception {
        String orderNumber = "ORD-12345";

        mockMvc.perform(post("/api/order/common/orders/{order-number}/payment-cancel", orderNumber))
               .andExpect(status().isNoContent());

        Mockito.verify(commonOrderService, times(1)).cancelOrderByOrderNumber(eq(orderNumber));
    }

    @Test
    @DisplayName("POST /api/order/common/orders/{order-id}/refund - 주문 환불 (204)")
    void refund_success() throws Exception {
        Long orderId = 3L;
        // 최소 필드만 포함한 더미 JSON (필드명이 다르면 빈 JSON으로 보내도 된다)
        String body = "{}";

        mockMvc.perform(post("/api/order/common/orders/{order-id}/refund", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isNoContent());

        Mockito.verify(commonOrderService, times(1))
               .refundOrderById(eq(orderId), any(ReturnRequestCreateRequestDto.class));
    }

    @Test
    @DisplayName("POST /api/order/common/payment-success/{order-number} - 결제 완료 처리 (200)")
    @Disabled
    void paymentSuccess_success() throws Exception {
        String orderNumber = "ORD-99999";

        mockMvc.perform(post("/api/order/common/payment-success/{order-number}", orderNumber))
               .andExpect(status().isNoContent());

        Mockito.verify(commonOrderService, times(1))
               .updateOrderPaymentByOrderNumber(eq(orderNumber));
    }
}
