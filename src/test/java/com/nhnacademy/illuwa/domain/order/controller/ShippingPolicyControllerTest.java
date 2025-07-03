package com.nhnacademy.illuwa.domain.order.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.order.controller.admin.ShippingPolicyController;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.service.ShippingPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = ShippingPolicyController.class)
class ShippingPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShippingPolicyService shippingPolicyService;

    private ShippingPolicyCreateRequestDto requestDto;
    private ShippingPolicyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new ShippingPolicyCreateRequestDto(new BigDecimal("30000"), new BigDecimal("3000"));
        responseDto = new ShippingPolicyResponseDto(1L, new BigDecimal("30000"), new BigDecimal("3000"));
    }

    @Test
    @DisplayName("POST /shipping-policy - 배송 정책 생성")
    void createShippingPolicy() throws Exception {
        Mockito.when(shippingPolicyService.addShippingPolicy(Mockito.any(ShippingPolicyCreateRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/shipping-policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shippingPolicyId").value(1L))
                .andExpect(jsonPath("$.minAmount").value(30000))
                .andExpect(jsonPath("$.fee").value(3000));
    }

    @Test
    @DisplayName("GET /shipping-policy - 배송 정책 단일 조회")
    void getShippingPolicy() throws Exception {
        Mockito.when(shippingPolicyService.getShippingPolicy(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/shipping-policy/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingPolicyId").value(1L))
                .andExpect(jsonPath("$.minAmount").value(30000))
                .andExpect(jsonPath("$.fee").value(3000));
    }

    @Test
    @DisplayName("GET /shipping-policy - 배송 정책 전체 조회 ")
    void getAllShippingPolicy() throws Exception {
        Mockito.when(shippingPolicyService.getShippingPolicyByActive(true))
                .thenReturn(responseDto);

        mockMvc.perform(get("/shipping-policy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("shippingPolicyId").value(1L));
    }

    @Test
    @DisplayName("PUT /shipping-policy/{id} - 배송 정책 수정")
    void updatePackaging() throws Exception {
        ShippingPolicyCreateRequestDto updateDto =
                new ShippingPolicyCreateRequestDto(new BigDecimal("25000"), new BigDecimal("5000"));

        ShippingPolicyResponseDto updatedResponse =
                new ShippingPolicyResponseDto(1L, new BigDecimal("25000"), new BigDecimal("5000"));

        Mockito.when(shippingPolicyService.updateShippingPolicy(Mockito.eq(1L),
                Mockito.any(ShippingPolicyCreateRequestDto.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/shipping-policy/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minAmount").value(25000))
                .andExpect(jsonPath("$.fee").value(5000));
    }

    @Test
    @DisplayName("DELETE /shipping-policy/{id} - 배송 정책 비활성화")
    void deleteShippingPolicy() throws Exception {
        Mockito.when(shippingPolicyService.removeShippingPolicy(1L)).thenReturn(1);

        mockMvc.perform(delete("/shipping-policy/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
