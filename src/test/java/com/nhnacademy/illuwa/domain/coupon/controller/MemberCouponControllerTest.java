package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.domain.coupons.controller.MemberCouponController;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.createMemberCouponRequest;
import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.memberCouponResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberCouponController.class)
class MemberCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberCouponService memberCouponService;

    String json;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        json = objectMapper.writeValueAsString(createMemberCouponRequest());
    }

    @Test
    @DisplayName("POST /members/member-coupons || (회원 쿠폰 발급 테스트)")
    void issueCouponTest() throws Exception {
        Mockito.when(memberCouponService.issueCoupon(Mockito.any()))
                .thenReturn(memberCouponResponse());

        mockMvc.perform(post("/members/member-coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.memberCouponId").value(1L))
                .andExpect(jsonPath("$.memberName").value("testName"))
                .andExpect(jsonPath("$.memberEmail").value("test@test.com"))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$.couponCode").value("testCode"))
                .andExpect(jsonPath("$.used").value(false))
                .andExpect(jsonPath("$.issuedAt").value(String.valueOf(LocalDate.now())))
                .andExpect(jsonPath("$.expiresAt").value(String.valueOf(LocalDate.now().plusMonths(1L))))
                .andExpect(jsonPath("$.usedAt").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("GET /members/member-coupons{email} || (회원 쿠폰 발급내역 조회)")
    void gettAllMemberCouponsTest() throws Exception {
        List<MemberCouponResponse> memberCoupons = List.of(
                memberCouponResponse(),
                memberCouponResponse().toBuilder()
                        .memberCouponId(2L)
                        .couponName("테 스 트 쿠 폰 이 름 임 2")
                        .build()
        );

        Mockito.when(memberCouponService.getAllMemberCoupons("test@test.com"))
                .thenReturn(memberCoupons);

        mockMvc.perform(get("/members/member-coupons/{email}", "test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberCouponId").value(1L))
                .andExpect(jsonPath("$[0].couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$[1].memberCouponId").value(2L))
                .andExpect(jsonPath("$[1].couponName").value("테 스 트 쿠 폰 이 름 임 2"))
                .andDo(print());
    }

    @Test
    @DisplayName("PUT /members/member-coupons/{email}/{memberCouponId}/use || (회원 쿠폰 사용)")
    void useCouponTest() throws Exception {
        MemberCouponUseResponse response = MemberCouponUseResponse.builder()
                .memberName("testName")
                .email("test@test.com")
                .couponName("testCouponName")
                .used(true)
                .usedAt(LocalDate.now())
                .build();

        Mockito.when(memberCouponService.useCoupon("test@test.com", 1L))
                .thenReturn(response);

        mockMvc.perform(put("/members/member-coupons/{email}/{memberCouponId}/use", "test@test.com", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberName").value("testName"))
                .andExpect(jsonPath("$.used").value(true))
                .andExpect(jsonPath("$.usedAt").value(String.valueOf(LocalDate.now())));

    }
}
