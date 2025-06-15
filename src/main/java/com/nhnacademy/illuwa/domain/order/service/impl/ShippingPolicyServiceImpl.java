package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ActiveShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyUpdateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import com.nhnacademy.illuwa.domain.order.service.ShippingPolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShippingPolicyServiceImpl implements ShippingPolicyService {

    private final ShippingPolicyRepository shippingPolicyRepository;

    public ShippingPolicyServiceImpl(ShippingPolicyRepository shippingPolicyRepository) {
        this.shippingPolicyRepository = shippingPolicyRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ActiveShippingPolicyDto> getAllShippingPolicy() {
        return shippingPolicyRepository.findAll().stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveShippingPolicyDto> getShippingPolicyByActive() {
        return shippingPolicyRepository.findByActive(true).stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ActiveShippingPolicyDto getShippingPolicy(String shippingPolicyId) {
        long id = parseId(shippingPolicyId);
        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByShippingPolicyId(id)
                .orElseThrow(() -> new NotFoundException("해당 배송 정책을 찾을 수 없습니다.", id));

        return toResponseDto(shippingPolicy);
    }

    @Override
    public ShippingPolicy addShippingPolicy(ShippingPolicyCreateRequestDto shippingPolicyCreateDto) {
        ShippingPolicy sp = ShippingPolicy.builder()
                .minAmount(shippingPolicyCreateDto.getMinAmount())
                .fee(shippingPolicyCreateDto.getFee())
                .active(true)
                .build();
        return shippingPolicyRepository.save(sp);
    }

    @Override
    public int removeShippingPolicy(String shippingPolicyId) {
        long id = parseId(shippingPolicyId);
        return shippingPolicyRepository.updateActiveByPackagingId(id, false);
    }

    @Override
    public ShippingPolicy updateShippingPolicy(String shippingPolicyId, ShippingPolicyCreateRequestDto shippingPolicyCreateDto) {
        long id = parseId(shippingPolicyId);
        shippingPolicyRepository.updateActiveByPackagingId(id, false);
        return addShippingPolicy(shippingPolicyCreateDto);
    }

    // Entity -> Dto
    private ActiveShippingPolicyDto toResponseDto(ShippingPolicy pkg) {
        return new ActiveShippingPolicyDto(pkg.getShippingPolicyId(),
                pkg.getMinAmount(),
                pkg.getFee()
        );
    }

    // ID 파싱 오류
    private long parseId(String shippingPolicyId) {
        try {
            return Long.parseLong(shippingPolicyId);
        } catch (NumberFormatException e) {
            throw new BadRequestException("유효하지 않은 배송비 정책 ID" + shippingPolicyId);
        }
    }
}
