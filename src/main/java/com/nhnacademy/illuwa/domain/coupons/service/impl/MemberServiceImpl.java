package com.nhnacademy.illuwa.domain.coupons.service.impl;

import com.nhnacademy.illuwa.domain.coupons.dto.member.MemberCreateRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.member.MemberResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.Member;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberRepository;
import com.nhnacademy.illuwa.domain.coupons.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    @Override
    public MemberResponse createMember(MemberCreateRequest createRequest) {

        Member member = Member.builder()
                .birth(createRequest.getBirth())
                .email(createRequest.getEmail())
                .name(createRequest.getName())
                .build();

        Member save = memberRepository.save(member);
        return MemberResponse.fromEntity(save);

    }

}
