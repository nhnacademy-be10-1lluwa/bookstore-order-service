package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.coupons.controller.CouponPolicyController;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyUpdateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.couponPolicy.CouponPolicyUpdateResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.status.DiscountType;
import com.nhnacademy.illuwa.domain.coupons.service.CouponPolicyService;
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

import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CouponPolicyController.class)
class CouponPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponPolicyService couponPolicyService;

    @Autowired
    private ObjectMapper objectMapper;

    String json;

    @BeforeEach
    void setup() throws JsonProcessingException {
        json = objectMapper.writeValueAsString(createPolicyRequest());
    }

    @Test
    @DisplayName("POST /admin/coupon-policies")
    void registerCouponPolicyTest() throws Exception {
        // given
        Mockito.when(couponPolicyService.createPolicy(Mockito.any()))
                .thenReturn(createPolicyResponse());

        // when & then
        mockMvc.perform(post("/api/admin/coupon-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.code").value("testCode"))
                .andExpect(status().isCreated());
        // createResponse.getCode()
    }

    @Test
    @DisplayName("GET /admin/coupon-policies/{id}")
    void getCouponPolicyTest() throws Exception {
        // given
        Mockito.when(couponPolicyService.getPolicyById(Mockito.any()))
                .thenReturn(policyResponse());

        // when & then
        mockMvc.perform(get("/api/admin/coupon-policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.code").value("testCode"))
                .andExpect(jsonPath("$.minOrderAmount").value(policyResponse().getMinOrderAmount()))
                .andExpect(jsonPath("$.discountAmount").value(policyResponse().getDiscountAmount()))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("GET /admin/coupon-policies?code={code}")
    void getByIdCouponPolicyTest() throws Exception {
        Mockito.when(couponPolicyService.getPolicyByCode(Mockito.any()))
                .thenReturn(policyResponse());

        mockMvc.perform(get("/api/admin/coupon-policies?code=testCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.id").value(policyResponse().getId()))
                .andExpect(jsonPath("$.minOrderAmount").value(policyResponse().getMinOrderAmount()))
                .andExpect(jsonPath("$.discountAmount").value(policyResponse().getDiscountAmount()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /admin/coupon-policies/{code}")
    void updateCouponPolicy() throws Exception {

        // 업데이트 요청 객체
        CouponPolicyUpdateRequest updateRequest = CouponPolicyUpdateRequest.builder()
                .minOrderAmount(BigDecimal.valueOf(1_000))
                .discountAmount(BigDecimal.valueOf(500))
                .build();

        // 업데이트 응답 객체
        CouponPolicyUpdateResponse updatedResponse = CouponPolicyUpdateResponse.builder()
                .code(createPolicyRequest().getCode())
                .minOrderAmount(updateRequest.getMinOrderAmount())
                .discountAmount(updateRequest.getDiscountAmount())
                .build();

        // Mock: 서비스가 이 응답을 리턴하게 설정
        Mockito.when(couponPolicyService.updatePolicy(Mockito.eq("testCode"), Mockito.any()))
                .thenReturn(updatedResponse);

        String tempJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/admin/coupon-policies/testCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tempJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("testCode"))
                .andExpect(jsonPath("$.minOrderAmount").value(1_000))
                .andExpect(jsonPath("$.discountAmount").value(500));
    }

    @Test
    @DisplayName("DELETE /admin/coupon-policies/AMT15K_DC3K")
    void deletePolicy() throws Exception {
        Mockito.doNothing().when(couponPolicyService).deletePolicy("testCode");

        mockMvc.perform(delete("/api/admin/coupon-policies/testCode"))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("GET /admin/coupon-policies")
    void getAllCouponPolicyTest() throws Exception {
        List<CouponPolicyResponse> list = List.of(CouponPolicyResponse.builder()
                        .id(1L)
                        .code("testCode1")
                        .minOrderAmount(BigDecimal.valueOf(20_000))
                        .discountAmount(BigDecimal.valueOf(3_000))
                        .build(),
                CouponPolicyResponse.builder()
                        .id(2L)
                        .code("testCode2")
                        .minOrderAmount(BigDecimal.valueOf(30_000))
                        .discountPercent(BigDecimal.valueOf(30))
                        .build());

        Mockito.when(couponPolicyService.getAllPolicies()).thenReturn(list);

        mockMvc.perform(get("/api/admin/coupon-policies"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("[0].id").value(1L))
                .andExpect(jsonPath("[1].id").value(2L))
                .andExpect(jsonPath("[0].code").value("testCode1"))
                .andExpect(jsonPath("[1].code").value("testCode2"))
                .andExpect(status().isOk());


    }
}
