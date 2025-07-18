package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.coupons.controller.CouponController;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponUpdateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponUpdateResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@WebMvcTest(controllers = CouponController.class)
@Disabled
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
    @DisplayName("GET /coupons?policyCode={code} || (정책기반) 쿠폰 목록 조회 {policyCode}")
    void getCouponsByPolicyCodeTest() throws Exception {
        List<CouponResponse> responseList = createCouponResponses(3);

        Mockito.when(couponService.getCouponsByPolicyCode("testCode"))
                .thenReturn(responseList);

        mockMvc.perform(get("/api/coupons?policyCode=testCode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].code").value("testCode"))
                .andExpect(jsonPath("$[1].code").value("testCode"))
                .andExpect(jsonPath("$[2].code").value("testCode"));
    }

    @Test
    @DisplayName("GET /coupons?type={TYPE} || (정책기반) 쿠폰 목록 조회 {TYPE}")
    void getCouponsByTypeTest() throws Exception {
        List<CouponResponse> responseList = createCouponResponses(3);

        Mockito.when(couponService.getCouponsByType(CouponType.GENERAL))
                .thenReturn(responseList);

        mockMvc.perform(get("/api/coupons?type=GENERAL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].couponType").value("GENERAL"))
                .andExpect(jsonPath("$[1].couponType").value("GENERAL"))
                .andExpect(jsonPath("$[2].couponType").value("GENERAL"));
    }

    @Test
    @DisplayName("GET /coupons?name={NAME} || (정책기반) 쿠폰 목록 조회 {NAME}")
    void getCouponsByNameTest() throws Exception {
        List<CouponResponse> responseList = createCouponResponses(2);

        Mockito.when(couponService.getCouponsByName("couponTestName"))
                .thenReturn(responseList);

        mockMvc.perform(get("/api/coupons?name=couponTestName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponName").value("couponTestName"))
                .andExpect(jsonPath("$[1].couponName").value("couponTestName"));
    }

    @Test
    @DisplayName("GET /coupons || (정책기반) 쿠폰 전체 조회")
    void getAllCouponsTest() throws Exception {
        List<CouponResponse> responseList = createCouponResponses(5);

        Mockito.when(couponService.getAllCoupons()).thenReturn(responseList);

        mockMvc.perform(get("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }


}
