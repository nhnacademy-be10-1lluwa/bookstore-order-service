package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.member.MemberCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.member.MemberResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.message.CouponEventPublisher;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final CouponEventPublisher couponEventPublisher;

    @Override
    public MemberResponse createMember(MemberCreateRequest createRequest) {
        // 여기서 req를 통해 사용자가 입력한 값을 토대로 엔티티를 생성
        Member member = Member.builder()
                .birth(createRequest.getBirth())
                .email(createRequest.getEmail())
                .name(createRequest.getName())
                .build();

        // 그 엔티티를 repo(=DB에 저장)
        Member save = memberRepository.save(member);

        // 회원가입 성공 시 RabbitMQ로 이벤트 발송
        couponEventPublisher.sendWelcomeCouponEvent(member.getEmail());
        return MemberResponse.fromEntity(save);
    }

}
