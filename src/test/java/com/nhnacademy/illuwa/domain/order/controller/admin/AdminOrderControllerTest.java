package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminOrderService;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    @InjectMocks
    private AdminOrderController controller;

    @Mock
    private AdminOrderService adminOrderService;

    @Mock
    private CommonOrderService commonOrderService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GET /api/order/admin/orders (전체 조회) - 200")
    void getOrderAll_success() throws Exception {
        Page<OrderListResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        Mockito.when(adminOrderService.getAllOrders(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/order/admin/orders").param("page","0").param("size","10"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/order/admin/orders/{order-id} - 단건 상세 조회 200")
    void getOrderDetail_success() throws Exception {
        OrderResponseDto dto = Mockito.mock(OrderResponseDto.class);
        Mockito.when(adminOrderService.getOrderByOrderId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/order/admin/orders/{order-id}", 1L))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/order/admin/orders?status=Shipped - 상태별 조회 200")
    void getOrdersByStatus_success() throws Exception {
        Page<OrderListResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
        Mockito.when(adminOrderService.getOrderByOrderStatus(eq(OrderStatus.Shipped), any()))
               .thenReturn(emptyPage);

        mockMvc.perform(get("/api/order/admin/orders")
                .param("status", OrderStatus.Shipped.name())
                .param("page","0")
                .param("size","20"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(0)))
               .andExpect(jsonPath("$.page", is(0)))
               .andExpect(jsonPath("$.size", is(20)))
               .andExpect(jsonPath("$.totalElements", is(0)))
               .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    @DisplayName("GET /api/order/admin/orders/by-number/{order-number} - 주문번호로 조회 200")
    void getOrderByNumber_success() throws Exception {
        OrderResponseDto dto = Mockito.mock(OrderResponseDto.class);
        Mockito.when(adminOrderService.getOrderByNumber("ORD-001")).thenReturn(dto);

        mockMvc.perform(get("/api/order/admin/orders/by-number/{order-number}", "ORD-001"))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/order/admin/orders/{order-id} - Shipped 시 배송일 업데이트 & 상태 변경 204")
    void updateOrderStatus_shipped_success() throws Exception {
        String body = "{\"orderStatus\":\"Shipped\"}";

        mockMvc.perform(put("/api/order/admin/orders/{order-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isNoContent());

        Mockito.verify(adminOrderService, times(1))
               .updateOrderDeliveryDate(eq(1L), any(LocalDate.class));
        Mockito.verify(commonOrderService, times(1))
               .updateOrderStatus(eq(1L), any(OrderUpdateStatusDto.class));
    }

    @Test
    @DisplayName("PUT /api/order/admin/orders/{order-id} - Shipped 아닌 경우 배송일 업데이트 없음 204")
    void updateOrderStatus_notShipped_success() throws Exception {
        String body = "{\"orderStatus\":\"Pending\"}";

        mockMvc.perform(put("/api/order/admin/orders/{order-id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isNoContent());

        Mockito.verify(adminOrderService, times(0))
               .updateOrderDeliveryDate(anyLong(), any(LocalDate.class));
        Mockito.verify(commonOrderService, times(1))
               .updateOrderStatus(eq(2L), any(OrderUpdateStatusDto.class));
    }
}
