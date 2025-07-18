package com.nhnacademy.illuwa.domain.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.illuwa.TestNoBatchApplication;
import com.nhnacademy.illuwa.domain.coupons.controller.MemberCouponController;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberCouponController.class)
@ContextConfiguration(classes = TestNoBatchApplication.class)
class MemberCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberCouponService memberCouponService;

    private MemberCouponCreateRequest createRequest;
    private MemberCouponResponse couponResponse;

    @BeforeEach
    void setup() {
        createRequest = MemberCouponCreateRequest.builder()
                .couponName("테 스 트 쿠 폰 이 름 임")
                .build();

        couponResponse = MemberCouponResponse.builder()
                .memberCouponId(1L)
                .memberId(1L)
                .couponName("테 스 트 쿠 폰 이 름 임")
                .couponCode("testCode")
                .used(false)
                .issuedAt(LocalDate.now())
                .expiresAt(LocalDate.now().plusMonths(1L))
                .build();
    }

    @Test
    @DisplayName("POST /api/member-coupons - 회원 쿠폰 발급")
    void issueCouponTest() throws Exception {
        Mockito.when(memberCouponService.issueCoupon(eq(1L), any(MemberCouponCreateRequest.class)))
                .thenReturn(couponResponse);

        mockMvc.perform(post("/api/member-coupons")
                        .header("X-USER-ID", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberCouponId").value(1L))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$.couponCode").value("testCode"))
                .andExpect(jsonPath("$.used").value(false))
                .andExpect(jsonPath("$.issuedAt").exists())
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("GET /api/member-coupons - 회원 쿠폰 리스트 조회")
    void getAllMemberCouponsTest() throws Exception {
        MemberCouponResponse couponResponse2 = MemberCouponResponse.builder()
                .memberCouponId(2L)
                .memberId(1L)
                .couponName("쿠폰2")
                .couponCode("code2")
                .used(false)
                .issuedAt(LocalDate.now())
                .expiresAt(LocalDate.now().plusMonths(1L))
                .build();

        List<MemberCouponResponse> responseList = List.of(couponResponse, couponResponse2);

        Mockito.when(memberCouponService.getAllMemberCoupons(1L)).thenReturn(responseList);

        mockMvc.perform(get("/api/member-coupons")
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberCouponId").value(1L))
                .andExpect(jsonPath("$[1].memberCouponId").value(2L));
    }

    @Test
    @DisplayName("PUT /api/member-coupons/{memberCouponId}/use - 회원 쿠폰 사용")
    void useCouponTest() throws Exception {
        MemberCouponUseResponse useResponse = MemberCouponUseResponse.builder()
                .memberId(1L)
                .couponName("테 스 트 쿠 폰 이 름 임")
                .used(true)
                .usedAt(LocalDate.now())
                .build();

        Mockito.when(memberCouponService.useCoupon(1L, 1L)).thenReturn(useResponse);

        mockMvc.perform(put("/api/member-coupons/{memberCouponId}/use", 1L)
                        .header("X-USER-ID", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
                .andExpect(jsonPath("$.used").value(true))
                .andExpect(jsonPath("$.usedAt").exists());
    }

    @Test
    @DisplayName("GET /api/member-coupons/book/{bookId}?couponType=BOOKS - 특정 도서에 적용 가능한 쿠폰 조회")
    void getBookCouponTest() throws Exception {
        MemberCouponDto dto = new MemberCouponDto(
                1L,
                10L,
                "자바쿠폰",
                CouponType.BOOKS
                , new BigDecimal("3000"),
                null
        );

        List<MemberCouponDto> dtos = List.of(dto);

        Mockito.when(memberCouponService.getAvailableCouponsForBook(
                eq(1L),
                eq(10L),
                eq(CouponType.BOOKS)
        )).thenReturn(dtos);

        mockMvc.perform(get("/api/member-coupons/book/{bookId}", 10L)
                        .header("X-USER-ID", 1L)
                        .param("couponType", "BOOKS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].memberCouponId").value(1))
                .andExpect(jsonPath("$[0].couponId").value(10))
                .andExpect(jsonPath("$[0].couponName").value("자바쿠폰"))
                .andExpect(jsonPath("$[0].couponType").value("BOOKS"))
                .andExpect(jsonPath("$[0].discountAmount").value(3000));
    }

    @Test
    @DisplayName("GET /api/member-coupons/category/{categoryId}?couponType=CATEGORY - 특정 카테고리에 적용 가능한 쿠폰 조회")
    void getCategoryCouponTest() throws Exception {
        MemberCouponDto dto = new MemberCouponDto(
                1L,
                11L,
                "프로그래밍쿠폰",
                CouponType.CATEGORY,
                new BigDecimal("3000"),
                null
        );

        List<MemberCouponDto> dtos = List.of(dto);

        Mockito.when(memberCouponService.getAvailableCouponsForCategory(
                eq(1L),
                eq(11L),
                eq(CouponType.CATEGORY)
        )).thenReturn(dtos);

        mockMvc.perform(get("/api/member-coupons/category/{categoryId}", 11L)
                        .header("X-USER-ID", 1L)
                        .param("couponType", "CATEGORY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].memberCouponId").value(1))
                .andExpect(jsonPath("$[0].couponId").value(11))
                .andExpect(jsonPath("$[0].couponName").value("프로그래밍쿠폰"))
                .andExpect(jsonPath("$[0].couponType").value("CATEGORY"))
                .andExpect(jsonPath("$[0].discountAmount").value(3000));
    }
}