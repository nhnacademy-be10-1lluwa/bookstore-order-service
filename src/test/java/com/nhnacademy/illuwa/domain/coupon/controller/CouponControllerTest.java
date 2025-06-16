package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.coupons.controller.CouponController;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
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
import java.util.Collections;
import java.util.List;

import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    String json;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        json = objectMapper.writeValueAsString(createCouponRequest());
    }

    @Test
    @DisplayName("POST /coupons || (정책기반) 쿠폰 생성 테스트")
    void registerCouponTest() throws Exception {
        Mockito.when(couponService.createCoupon(Mockito.any()))
                .thenReturn(createCouponResponse());

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /coupons/{id} || (정책기반) 쿠폰 단건 조회 {id}")
    void getCouponTest() throws Exception {
        Mockito.when(couponService.getCouponById(Mockito.any()))
                .thenReturn(couponResponse());

        mockMvc.perform(get("/coupons/" + couponResponse().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$.code").value("testCode"))
                .andExpect(jsonPath("$.couponType").value("WELCOME"))
                .andExpect(jsonPath("$.issueCount").value(BigDecimal.valueOf(100)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /coupons?policyCode={code} || (정책기반) 쿠폰 목록 조회 {policyCode}")
    void getCouponsByPolicyCodeTest() throws Exception {
        List<CouponResponse> responseList = createCouponResponses(3);

        Mockito.when(couponService.getCouponsByPolicyCode(Mockito.any()))
                .thenReturn(responseList);

        mockMvc.perform(get("/coupons?policyCode=testCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].couponName").value("목록 테스트 쿠폰 1"))
                .andExpect(jsonPath("$[1].couponName").value("목록 테스트 쿠폰 2"))
                .andExpect(jsonPath("$[2].couponName").value("목록 테스트 쿠폰 3"));
    }

    @Test
    @DisplayName("GET /coupons?type={TYPE} || (정책기반) 쿠폰 목록 조회 {TYPE}")
    void getCouponsByTypeTest() throws Exception {
        List<CouponResponse> responseList = createCouponResponses(3);

        Mockito.when(couponService.getCouponsByType(Mockito.any()))
                .thenReturn(responseList);

        mockMvc.perform(get("/coupons?policyCode=testCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].couponName").value("목록 테스트 쿠폰 1"))
                .andExpect(jsonPath("$[1].couponName").value("목록 테스트 쿠폰 2"))
                .andExpect(jsonPath("$[2].couponName").value("목록 테스트 쿠폰 3"));
    }
//
//    @Test
//    @DisplayName("GET /coupons?name={name} || (정책기반) 쿠폰 목록 조회 {NAME}")
//    public getCouponsByNameTest() throws Exception {
//
//    }
}
