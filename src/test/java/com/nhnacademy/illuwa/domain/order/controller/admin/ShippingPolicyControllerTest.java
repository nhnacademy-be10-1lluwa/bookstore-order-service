package com.nhnacademy.illuwa.domain.order.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.service.ShippingPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ShippingPolicyControllerTest {

    @InjectMocks
    private ShippingPolicyController controller;

    @Mock
    private ShippingPolicyService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ShippingPolicyCreateRequestDto requestDto;
    private ShippingPolicyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        requestDto = new ShippingPolicyCreateRequestDto(new BigDecimal("30000"), new BigDecimal("3000"));
        responseDto = new ShippingPolicyResponseDto(1L, new BigDecimal("30000"), new BigDecimal("3000"));
    }

    @Test
    @DisplayName("POST /api/shipping-policy - 생성 성공 시 201")
    void createShippingPolicy_success() throws Exception {
        Mockito.when(service.addShippingPolicy(any(ShippingPolicyCreateRequestDto.class)))
               .thenReturn(responseDto);

        mockMvc.perform(post("/api/shipping-policy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isCreated())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$.shippingPolicyId", is(1)))
               .andExpect(jsonPath("$.minAmount", is(30000)))
               .andExpect(jsonPath("$.fee", is(3000)));
    }

    @Test
    @DisplayName("GET /api/shipping-policy/{id} - 단일 정책 조회")
    void getShippingPolicy_success() throws Exception {
        Mockito.when(service.getShippingPolicy(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/shipping-policy/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.shippingPolicyId", is(1)))
               .andExpect(jsonPath("$.minAmount", is(30000)))
               .andExpect(jsonPath("$.fee", is(3000)));
    }

    @Test
    @DisplayName("GET /api/shipping-policy - 활성 정책 조회")
    void getActiveShippingPolicy_success() throws Exception {
        Mockito.when(service.getShippingPolicyByActive(true)).thenReturn(responseDto);

        mockMvc.perform(get("/api/shipping-policy"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.shippingPolicyId", is(1)))
               .andExpect(jsonPath("$.minAmount", is(30000)))
               .andExpect(jsonPath("$.fee", is(3000)));
    }

    @Test
    @DisplayName("PUT /api/shipping-policy/{id} - 정책 수정")
    void updateShippingPolicy_success() throws Exception {
        ShippingPolicyCreateRequestDto updateDto =
                new ShippingPolicyCreateRequestDto(new BigDecimal("25000"), new BigDecimal("5000"));
        ShippingPolicyResponseDto updatedResponse =
                new ShippingPolicyResponseDto(1L, new BigDecimal("25000"), new BigDecimal("5000"));

        Mockito.when(service.updateShippingPolicy(eq(1L), any(ShippingPolicyCreateRequestDto.class)))
               .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/shipping-policy/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.minAmount", is(25000)))
               .andExpect(jsonPath("$.fee", is(5000)));
    }

    @Test
    @DisplayName("DELETE /api/shipping-policy/{id} - 정책 비활성화 (204)")
    void deleteShippingPolicy_success() throws Exception {
        Mockito.when(service.removeShippingPolicy(1L)).thenReturn(1);

        mockMvc.perform(delete("/api/shipping-policy/{id}", 1L))
               .andExpect(status().isNoContent());
    }
}
