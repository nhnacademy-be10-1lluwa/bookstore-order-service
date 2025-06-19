package com.nhnacademy.illuwa.domain.order.repository.custom;


import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.AllShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;

import java.util.List;
import java.util.Optional;

public interface ShippingPolicyQuerydslRepository {

    List<AllShippingPolicyDto> findAllShippingDtosPolicy();

    List<ShippingPolicyResponseDto> findShippingPolicyDtosByActive(boolean active);

    Optional<ShippingPolicyResponseDto> findSHippingPolicyDtoByShippingPolicyId(long shippingPolicyId);

}
