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
    public List<ShippingPolicyResponseDto> getShippingPolicyByActive(boolean active) {
        return shippingPolicyRepository.findShippingPolicyDtosByActive(active);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingPolicyResponseDto getShippingPolicy(String shippingPolicyId) {
        long id = parseId(shippingPolicyId);
        return shippingPolicyRepository.findSHippingPolicyDtoByShippingPolicyId(id)
                .orElseThrow(() -> new NotFoundException("해당 배송 정책을 찾을 수 없습니다.", id));
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

    // ID 파싱 오류
    private long parseId(String shippingPolicyId) {
        try {
            return Long.parseLong(shippingPolicyId);
        } catch (NumberFormatException e) {
            throw new BadRequestException("유효하지 않은 배송비 정책 ID" + shippingPolicyId);
        }
    }
}
