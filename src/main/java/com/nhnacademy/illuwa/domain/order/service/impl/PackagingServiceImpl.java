package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PackagingServiceImpl implements PackagingService {

    private final PackagingRepository packagingRepository;

    public PackagingServiceImpl(PackagingRepository packagingRepository) {
        this.packagingRepository = packagingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackagingResponseDto> getAllPackaging() {
        return packagingRepository.findPackagingDtos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackagingResponseDto> getPackagingByActive(boolean active) {
        return packagingRepository.findPackagingDtosByActive(active);
    }

    @Override
    @Transactional(readOnly = true)
    public PackagingResponseDto getPackaging(Long packagingId) {
        return packagingRepository.findPackagingDtoById(packagingId).orElseThrow(()
                -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", packagingId));
    }

    @Override
    public PackagingResponseDto addPackaging(PackagingCreateRequestDto packagingCreateDto) {
        Packaging pkg = Packaging.builder()
                .packagingName(packagingCreateDto.getPackagingName())
                .packagingPrice(packagingCreateDto.getPackagingPrice())
                .active(true)
                .build();

        Packaging packaging = packagingRepository.save(pkg);

        return PackagingResponseDto.fromEntity(packaging);
    }

    @Override
    public int removePackaging(Long packagingId) {
        return packagingRepository.updateActiveByPackagingId(packagingId, false);
    }

    // fixme 삭제 로직 고려 후 수정
    @Override
    public PackagingResponseDto updatePackaging(Long packagingId, PackagingCreateRequestDto packagingCreateDto) {

        removePackaging(packagingId);

        return addPackaging(packagingCreateDto);
    }


    // Entity -> Dto
    private PackagingResponseDto toResponseDto(Packaging pkg) {
        return new PackagingResponseDto(pkg.getPackagingId(), pkg.getPackagingName(), pkg.getPackagingPrice());
    }

    // ID 파싱 오류(잘못된 숫자 포맷)
    private long parseId(String packagingId) {
        try {
            return Long.parseLong(packagingId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("유효하지 않은 포장 옵션 ID: " + packagingId);
        }
    }
}
