//package com.nhnacademy.illuwa.domain.coupon.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils;
//import com.nhnacademy.illuwa.domain.coupons.controller.MemberCouponController;
//import com.nhnacademy.illuwa.domain.coupons.dto.coupon.CouponInfoResponse;
//import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
//import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponUseResponse;
//import com.nhnacademy.illuwa.domain.coupons.entity.Coupon;
//import com.nhnacademy.illuwa.domain.coupons.entity.CouponPolicy;
//import com.nhnacademy.illuwa.domain.coupons.entity.Member;
//import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
//import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
//import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.cglib.core.Local;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.createMemberCouponRequest;
//import static com.nhnacademy.illuwa.domain.coupon.CouponPolicyTestUtils.memberCouponResponse;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = MemberCouponController.class)
//class MemberCouponControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private MemberCouponService memberCouponService;
//
//    String json;
//
//    @BeforeEach
//    void setUp() throws JsonProcessingException {
//        json = objectMapper.writeValueAsString(createMemberCouponRequest());
//    }
//
//    @Test
//    @DisplayName("POST /members/member-coupons || (회원 쿠폰 발급 테스트)")
//    void issueCouponTest() throws Exception {
//        Mockito.when(memberCouponService.issueCoupon(Mockito.any()))
//                .thenReturn(memberCouponResponse());
//
//        mockMvc.perform(post("/members/member-coupons")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(jsonPath("$.memberCouponId").value(1L))
//                .andExpect(jsonPath("$.memberName").value("testName"))
//                .andExpect(jsonPath("$.memberEmail").value("test@test.com"))
//                .andExpect(jsonPath("$.couponName").value("테 스 트 쿠 폰 이 름 임"))
//                .andExpect(jsonPath("$.couponCode").value("testCode"))
//                .andExpect(jsonPath("$.used").value(false))
//                .andExpect(jsonPath("$.issuedAt").value(String.valueOf(LocalDate.now())))
//                .andExpect(jsonPath("$.expiresAt").value(String.valueOf(LocalDate.now().plusMonths(1L))))
//                .andExpect(jsonPath("$.usedAt").doesNotExist())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("GET /members/member-coupons{email} || (회원 쿠폰 발급내역 조회)")
//    void gettAllMemberCouponsTest() throws Exception {
//        List<MemberCouponResponse> memberCoupons = List.of(
//                memberCouponResponse(),
//                memberCouponResponse().toBuilder()
//                        .memberCouponId(2L)
//                        .couponName("테 스 트 쿠 폰 이 름 임 2")
//                        .build()
//        );
//
//        Mockito.when(memberCouponService.getAllMemberCoupons("test@test.com"))
//                .thenReturn(memberCoupons);
//
//        mockMvc.perform(get("/members/member-coupons/{email}", "test@test.com"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].memberCouponId").value(1L))
//                .andExpect(jsonPath("$[0].couponName").value("테 스 트 쿠 폰 이 름 임"))
//                .andExpect(jsonPath("$[1].memberCouponId").value(2L))
//                .andExpect(jsonPath("$[1].couponName").value("테 스 트 쿠 폰 이 름 임 2"))
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("PUT /members/member-coupons/{email}/{memberCouponId}/use || (회원 쿠폰 사용)")
//    void useCouponTest() throws Exception {
//        MemberCouponUseResponse response = MemberCouponUseResponse.builder()
//                .memberName("testName")
//                .email("test@test.com")
//                .couponName("testCouponName")
//                .used(true)
//                .usedAt(LocalDate.now())
//                .build();
//
//        Mockito.when(memberCouponService.useCoupon("test@test.com", 1L))
//                .thenReturn(response);
//
//        mockMvc.perform(put("/members/member-coupons/{email}/{memberCouponId}/use", "test@test.com", 1))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.memberName").value("testName"))
//                .andExpect(jsonPath("$.used").value(true))
//                .andExpect(jsonPath("$.usedAt").value(String.valueOf(LocalDate.now())));
//    }
//
//    @Test
//    @DisplayName("POST /members/member-coupons/welcome?email={memberEmail)")
//    void 웰컴쿠폰발급() throws Exception {
//        // given
//        /**
//         * (임시) 회원 생성
//         */
//        Member member = Member.builder()
//                .id(1L)
//                .name("아무개")
//                .email("test@test.com")
//                .birth(LocalDate.of(1999, 6, 12))
//                .build();
//        /**
//         * (임시) 정책 생성
//         * code("testCode")
//         * minOrderAmount(BigDecimal.valueOf(20_000))
//         * discountType(DiscountType.AMOUNT)
//         * discountAmount(BigDecimal.valueOf(3_000))
//         */
//        CouponPolicy policy = CouponPolicyTestUtils.createPolicy();
//
//        /**
//         * (임시) 웰컴 쿠폰 생성
//         */
//        Coupon coupon = Coupon.builder()
//                .id(1L)
//                .couponName("웰컴쿠폰")
//                .policy(policy)
//                .validFrom(LocalDate.parse("2025-01-01"))
//                .validTo(LocalDate.parse("2025-12-31"))
//                .couponType(CouponType.WELCOME)
//                .comment("회원가입 대상자에 대한 쿠폰입니다.")
//                .conditions("모든 도서 포함 최소주문 20,000이상 시 3,000원 할인")
//                .issueCount(BigDecimal.valueOf(100))
//                .build();
//
//        MemberCoupon memberCoupon = MemberCoupon.builder()
//                .id(1L)
//                .member(member)
//                .coupon(coupon)
//                .build();
//
//        MemberCouponResponse response = MemberCouponResponse.fromEntity(memberCoupon);
//
//        // when
//        Mockito.when(memberCouponService.issueWelcomeCoupon("test@test.com")).thenReturn(response);
//
//
//        // then
//        mockMvc.perform(post("/members/member-coupons/welcome?email={memberEmail}", "test@test.com"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.couponName").value("웰컴쿠폰"));
//    }
//
//    @Test
//    @DisplayName("POST /members/member-coupons?memberId={memberId}")
//    void 회원쿠폰_조회()throws Exception {
//        List<MemberCouponResponse> memberCoupons = List.of(
//                memberCouponResponse(),
//                memberCouponResponse().toBuilder()
//                        .memberCouponId(2L)
//                        .couponName("테 스 트 쿠 폰 이 름 임 2")
//                        .build()
//        );
//
//        Mockito.when(memberCouponService.getAllMemberCoupons(1L))
//                .thenReturn(memberCoupons);
//
//        mockMvc.perform(get("/members/member-coupons")
//                        .param("memberId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].memberCouponId").value(1L))
//                .andExpect(jsonPath("$[0].couponName").value("테 스 트 쿠 폰 이 름 임"))
//                .andExpect(jsonPath("$[1].memberCouponId").value(2L))
//                .andExpect(jsonPath("$[1].couponName").value("테 스 트 쿠 폰 이 름 임 2"))
//                .andDo(print());
//
//    }
//
//    @Test
//    @DisplayName("GET /members/member-coupons/{memberCouponId}/coupon")
//    void 쿠폰정보_조회()throws Exception {
//        CouponInfoResponse infoResponse = CouponInfoResponse.builder()
//                .couponId(1L)
//                .couponName("웰컴쿠폰")
//                .couponType(CouponType.WELCOME)
//                .discountAmount(BigDecimal.valueOf(15000))
//                .issueCount(BigDecimal.valueOf(100))
//                .build();
//
//        Mockito.when(memberCouponService.getCouponInfoFromMemberCoupon(1L))
//                .thenReturn(infoResponse);
//
//        mockMvc.perform(get("/members/member-coupons/{memberCouponId}/coupon", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.couponId").value(1L))
//                .andExpect(jsonPath("$.couponName").value("웰컴쿠폰"))
//                .andExpect(jsonPath("$.discountAmount").value(15000))
//                .andExpect(jsonPath("$.issueCount").value(100));}
//
//}
