package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.coupons.controller.AdminCouponController;
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

import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCouponController.class)
@Disabled
class AdminCouponControllerTest {
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
    @DisplayName("POST /admin/coupons || (정책기반) 쿠폰 생성 테스트 ")
    void registerCouponTest() throws Exception {
        Mockito.when(couponService.createCoupon(Mockito.any()))
                .thenReturn(createCouponResponse());

        mockMvc.perform(post("/api/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /admin/coupons/{id} || (정책기반) 쿠폰 단건 조회 {id}")
    void getCouponTest() throws Exception {
        Mockito.when(couponService.getCouponById(Mockito.any()))
                .thenReturn(couponResponse());

        mockMvc.perform(get("/api/admin/coupons/" + couponResponse().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$.code").value("testCode"))
                .andExpect(jsonPath("$.couponType").value("WELCOME"))
                .andExpect(jsonPath("$.issueCount").value(BigDecimal.valueOf(100)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /admin/coupons/{id} || 쿠폰 수정 테스트")
    void updateCouponsTest() throws Exception {
        // 기존 쿠폰 응답 먼저 확인
        CouponResponse response = couponResponse(); // 기존 쿠폰 정보
        Mockito.when(couponService.getCouponById(1L)).thenReturn(response);

        // 기존 쿠폰 정보 확인
        mockMvc.perform(get("/api/admin/coupons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$.code").value("testCode"));

        // 업데이트할 쿠폰 정보 생성
        CouponUpdateRequest updateRequest = CouponUpdateRequest.builder()
                .couponName("테 스 트 쿠 폰 수 정 된 이 름 임") // 수정된 이름
                .build();

        // 업데이트된 응답(mock 응답값)
        CouponUpdateResponse updateResponse = CouponUpdateResponse.builder()
                .couponName("테 스 트 쿠 폰 수 정 된 이 름 임") // 수정된 이름
                .couponType(CouponType.GENERAL) // 쿠폰 타입
                .build();

        // mock 서비스 응답 설정
        Mockito.when(couponService.updateCoupon(Mockito.any(), Mockito.any())).thenReturn(updateResponse);

        // PUT 요청을 보내고 응답 확인
        mockMvc.perform(put("/api/admin/coupons/" + response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))) // updateRequest를 JSON으로 변환
                .andExpect(status().isOk()) // 응답 상태가 200 OK
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 수 정 된 이 름 임")) // 수정된 이름 확인
                .andExpect(jsonPath("$.couponType").value("GENERAL"));
    }

    @Test
    @DisplayName("DELETE /admin/coupons{id} || 쿠폰 삭제")
    void deleteCouponsTest() throws Exception {

        Mockito.doNothing().when(couponService).deleteCoupon(99L);

        mockMvc.perform(delete("/api/admin/coupons/1"))
                .andExpect(status().isNoContent());
    }
}
