package com.nhnacademy.illuwa.domain.order.repository.custom;

import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;

import java.util.List;
import java.util.Optional;

public interface PackagingQuerydslRepository {

    List<PackagingResponseDto> findPackagingDtos();

    Optional<PackagingResponseDto> findPackagingDtoById(Long orderId);

    List<PackagingResponseDto> findPackagingDtosByActive(boolean active);

}
