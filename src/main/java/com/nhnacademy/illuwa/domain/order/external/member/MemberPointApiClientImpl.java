package com.nhnacademy.illuwa.domain.order.external.member;


import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberPointDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberSavePointDto;
import com.nhnacademy.illuwa.domain.order.external.member.dto.MemberUsedPointDto;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class MemberPointApiClientImpl implements MemberPointApiClient {
    private final RestTemplate restTemplate;
//    private final RequestAttributes requestAttributes; <- 현재 쓰레드에서 진행 중인 HTTP 요청 관련 정보를 저장하거나 꺼낼 수 있도록 도와주는 인터페이스이다.

    private final OrderRepository orderRepository;

    @Value("${user-api.url}")
    private String memberApiUrl;

    public MemberPointApiClientImpl(RestTemplateBuilder builder, OrderRepository orderRepository) {
        this.restTemplate = builder.build();
        this.orderRepository = orderRepository;
    }

    @Override
    public MemberUsedPointDto sendUsedPointByMemberId(Long memberId, BigDecimal usedPoint) {

        String url = memberApiUrl + "/memberId/" + memberId + "/points/use";

        MemberUsedPointDto request = new MemberUsedPointDto(usedPoint);

        ResponseEntity<MemberUsedPointDto> response = restTemplate.postForEntity(url, request, MemberUsedPointDto.class);

        return Objects.requireNonNull(response.getBody()); // restTemplate 자체 ErrorHandler 가 예외 처리
    }

    @Override
    public MemberSavePointDto sendTotalPrice(Long memberId, BigDecimal totalPrice) {
        String url = memberApiUrl + "/memberId/" + memberId + "/points/earn";

        MemberSavePointDto request = new MemberSavePointDto(totalPrice);

        ResponseEntity<MemberSavePointDto> response = restTemplate.postForEntity(url, request, MemberSavePointDto.class);

        return Objects.requireNonNull(response.getBody()); // restTemplate 자체 ErrorHandler 가 예외 처리
    }

    @Override
    public Optional<MemberPointDto> getPointByMemberId(Long memberId) {
        String url = memberApiUrl + "/memberId/" + memberId + "/points";

        try {
            ResponseEntity<MemberPointDto> response = restTemplate.getForEntity(url, MemberPointDto.class);
            MemberPointDto body = response.getBody();
            return Optional.ofNullable(body);
        } catch (Exception e) {
            throw new IllegalArgumentException("해당 멤버의 보유 보인트 조회 실패 -> " + e.getMessage());
        }
    }

    @Override
    public List<MemberGradeUpdateRequest> sendNetOrderAmount() {
        String url = memberApiUrl + "/members/grades/update";

        List<MemberGradeUpdateRequest> request = orderRepository.findAllGradeDto();

        HttpEntity<List<MemberGradeUpdateRequest>> entity = new HttpEntity<>(request);

        ResponseEntity<List<MemberGradeUpdateRequest>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return Objects.requireNonNull(response.getBody());
    }

}
