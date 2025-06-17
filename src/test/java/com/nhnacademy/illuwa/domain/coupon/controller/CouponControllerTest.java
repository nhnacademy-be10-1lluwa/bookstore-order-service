package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.coupons.controller.CouponController;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponCreateResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponUpdateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponUpdateResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

        Mockito.when(couponService.getCouponsByPolicyCode("testCode"))
                .thenReturn(responseList);

        mockMvc.perform(get("/coupons?policyCode=testCode")
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

        mockMvc.perform(get("/coupons?type=GENERAL")
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

        mockMvc.perform(get("/coupons?name=couponTestName")
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

        mockMvc.perform(get("/coupons")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @DisplayName("PUT /coupons/{id} || 쿠폰 수정 테스트")
    void updateCouponsTest() throws Exception {
        // 기존 쿠폰 응답 먼저 확인
        CouponResponse response = couponResponse(); // 기존 쿠폰 정보
        Mockito.when(couponService.getCouponById(1L)).thenReturn(response);

        // 기존 쿠폰 정보 확인
        mockMvc.perform(get("/coupons/1")
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
        mockMvc.perform(put("/coupons/" + response.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))) // updateRequest를 JSON으로 변환
                .andExpect(status().isOk()) // 응답 상태가 200 OK
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 수 정 된 이 름 임")) // 수정된 이름 확인
                .andExpect(jsonPath("$.couponType").value("GENERAL"));
    }

    @Test
    @DisplayName("DELETE /coupons{id} || 쿠폰 삭제")
    void deleteCouponsTest() throws Exception {
//        CouponResponse response = couponResponse();
//        System.out.println(response.getId());

        Mockito.doNothing().when(couponService).deleteCoupon(99L);

        mockMvc.perform(delete("/coupons/1"))
                .andExpect(status().isNoContent());
    }
}
