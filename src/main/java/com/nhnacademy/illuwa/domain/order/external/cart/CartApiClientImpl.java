package com.nhnacademy.illuwa.domain.order.external.cart;

import com.nhnacademy.illuwa.domain.order.external.cart.dto.CartDto;
import com.nhnacademy.illuwa.domain.order.external.cart.dto.CartIdDto;
import com.nhnacademy.illuwa.domain.order.external.cart.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class CartApiClientImpl implements CartApiClient {

    private final RestTemplate restTemplate;

    @Value("${book-api.url}")
    private String cartServiceUrl;

    public CartApiClientImpl(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public Optional<CartDto> getCart(Long memberId) {

        String url = cartServiceUrl + "/해당 카트 api 주소";

        try {
            ResponseEntity<CartDto> response = restTemplate.getForEntity(url, CartDto.class);
            CartDto body = response.getBody();
            return Optional.ofNullable(body);
        } catch (Exception e) {
            throw new IllegalArgumentException("해당 멤버의 장바구니 조회 실패 ->" + e.getMessage());
        }
    }

    @Override
    public CartIdDto sendRemoveCartItem(Long memberId) {

        String url = cartServiceUrl + "/해당 카트 api 주소";

        CartIdDto request = new CartIdDto(memberId);

        ResponseEntity<CartIdDto> response = restTemplate.postForEntity(url, request, CartIdDto.class);

        return Objects.requireNonNull(response.getBody());
    }
}
