package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.AllShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
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
    public List<AllShippingPolicyDto> getAllShippingPolicy() {
        return shippingPolicyRepository.findAllShippingDtosPolicy();
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingPolicyResponseDto getShippingPolicyByActive(boolean active) {
        return shippingPolicyRepository.findShippingPolicyDtoByActive(active);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingPolicyResponseDto getShippingPolicy(Long shippingPolicyId) {
        return shippingPolicyRepository.findSHippingPolicyDtoByShippingPolicyId(shippingPolicyId)
                .orElseThrow(() -> new NotFoundException("해당 배송 정책을 찾을 수 없습니다.", shippingPolicyId));
    }

    @Override
    public ShippingPolicyResponseDto addShippingPolicy(ShippingPolicyCreateRequestDto shippingPolicyCreateDto) {
        ShippingPolicy sp = ShippingPolicy.builder()
                .minAmount(shippingPolicyCreateDto.getMinAmount())
                .fee(shippingPolicyCreateDto.getFee())
                .active(true)
                .build();

        ShippingPolicy shippingPolicy = shippingPolicyRepository.save(sp);

        return ShippingPolicyResponseDto.fromEntity(shippingPolicy);
    }

    @Override
    public int removeShippingPolicy(Long shippingPolicyId) {
        return shippingPolicyRepository.updateActiveByPackagingId(shippingPolicyId, false);
    }

    @Override
    public ShippingPolicyResponseDto updateShippingPolicy(Long shippingPolicyId, ShippingPolicyCreateRequestDto shippingPolicyCreateDto) {
        shippingPolicyRepository.updateActiveByPackagingId(shippingPolicyId, false);
        return addShippingPolicy(shippingPolicyCreateDto);
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
