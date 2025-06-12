package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PackagingServiceImpl implements PackagingService {

    private PackagingRepository packagingRepository;

    public PackagingServiceImpl(PackagingRepository packagingRepository) {
        this.packagingRepository = packagingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackagingResponseDto> getAllPackaging() {
        return packagingRepository.findAll().stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackagingResponseDto> getPackagingByActive() {
        return packagingRepository.findByActive(true).stream().map(this::toResponseDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PackagingResponseDto getPackaging(String packagingId) {
        Packaging packaging = packagingRepository.findByPackagingId(Long.valueOf(packagingId));
        return toResponseDto(packaging);
    }

    @Override
    public Packaging addPackaging(PackagingCreateRequestDto packagingCreateDto) {
        Packaging pkg = Packaging.builder()
                .packagingName(packagingCreateDto.getPackagingName())
                .packagingPrice(packagingCreateDto.getPackagingPrice())
                .active(true)
                .build();
        return packagingRepository.save(pkg);
    }

    @Override
    public int removePackaging(String packagingId) {
        long id = Long.parseLong(packagingId);
        return packagingRepository.updateActiveByPackagingId(id, false);
    }

    @Override
    public Packaging updatePackaging(String packagingId, PackagingCreateRequestDto packagingCreateDto) {
        int result = removePackaging(packagingId);
        return addPackaging(packagingCreateDto);
    }


    private PackagingResponseDto toResponseDto(Packaging pkg) {
        return new PackagingResponseDto(pkg.getPackagingId(), pkg.getPackagingName(), pkg.getPackagingPrice());

    }
}
