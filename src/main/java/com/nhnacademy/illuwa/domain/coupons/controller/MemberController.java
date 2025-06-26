package com.nhnacademy.illuwa.domain.coupons.controller;

import com.nhnacademy.illuwa.domain.coupons.dto.member.MemberCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.member.MemberResponse;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import com.nhnacademy.illuwa.domain.coupons.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@AllArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody @Valid MemberCreateRequest request) {
        MemberResponse response = memberService.createMember(request);

        RestTemplate restTemplate = new RestTemplate();
        // 해당 이메일을 가지고 도메인의 포트를 이용해 post메서드 실행
        // postForObject(요청 URL, 파라미터, 반환타입.class)
        String couponUrl = "http://localhost:8080/members/member-coupons/welcome?email=" + response.getEmail();
        restTemplate.postForObject(couponUrl,null, Object.class);
        return ResponseEntity.ok(response);
    }
}
