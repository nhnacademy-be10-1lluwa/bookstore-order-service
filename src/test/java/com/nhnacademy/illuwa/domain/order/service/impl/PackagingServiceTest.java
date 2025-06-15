// src/test/java/com/nhnacademy/illuwa/domain/order/service/impl/PackagingServiceIntegrationTest.java
package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional              // 각 테스트 후 롤백
@Rollback(value = false)
public class PackagingServiceTest {

    @Autowired
    private PackagingServiceImpl service;

    @Autowired
    private PackagingRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(Packaging.builder()
                .packagingName("Box")
                .packagingPrice(new BigDecimal("1000"))
                .active(true)
                .build());

        repository.save(Packaging.builder()
                .packagingName("Envelope")
                .packagingPrice(new BigDecimal("500"))
                .active(true)
                .build());

        repository.save(Packaging.builder()
                .packagingName("Nothing")
                .packagingPrice(new BigDecimal("1500"))
                .active(false)
                .build());
    }

    @Test
    @DisplayName("전체 조회 테스트")
    void testAllPackaging() {
        List<PackagingResponseDto> dtos = service.getAllPackaging();

        assertThat(dtos).hasSize(3);
        assertThat(dtos).extracting("packagingName")
                .containsExactlyInAnyOrder("Box", "Envelope", "Nothing");
    }

    @Test
    @DisplayName("활성된 포장 옵션 테스트")
    void testGetPackagingByActive() {
        List<PackagingResponseDto> dtos = service.getPackagingByActive();

        assertThat(dtos).allSatisfy(dto ->
                assertThat(dto.getPackagingPrice()).isGreaterThan(BigDecimal.ZERO)
        );

        assertThat(dtos.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("포장 옵션 추가 및 조회")
    void testAdd_and_getPackaging() {
        PackagingCreateRequestDto req = new PackagingCreateRequestDto("Plastic", new BigDecimal("200"));
        Packaging created = service.addPackaging(req);

        PackagingResponseDto dto = service.getPackaging(String.valueOf(created.getPackagingId()));
        assertThat(dto.getPackagingName()).isEqualTo("Plastic");
        assertThat(dto.getPackagingPrice()).isEqualByComparingTo(new BigDecimal("200"));
    }


   /* @Test
    @DisplayName("H2 DB 직접 조회 테스트")
    void testDirectH2Access() {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM packaging", Integer.class
        );
        assertThat(count).isEqualTo(2);
    }*/

    @Test
    @DisplayName("포장 옵션 비활성화")
    void testRemovePackaging() {

        List<Packaging> savedList = repository.findAll();
        assertThat(savedList).hasSize(3);

        Long targetId = savedList.get(0).getPackagingId();


        int removed = service.removePackaging(targetId.toString());

        assertThat(removed).isEqualTo((int) 1);
        Boolean isActive = jdbcTemplate.queryForObject(
                String.format("SELECT active FROM packaging WHERE packaging_id = %d", targetId) , Boolean.class
        );
        assertThat(isActive).isFalse();
    }

    @Test
    @DisplayName("포장 옵션 수정")
    void testUpdatePackaging() {

        List<Packaging> savedList = repository.findAll();
        assertThat(savedList).hasSize(3);

        Long targetId = savedList.getFirst().getPackagingId();

        Packaging packaging = service.updatePackaging(targetId.toString(), new PackagingCreateRequestDto("Something", new BigDecimal("10000")));

        Boolean isActive = jdbcTemplate.queryForObject(
                String.format("SELECT active FROM packaging WHERE packaging_id = %d", targetId) , Boolean.class
        );
        assertThat(isActive).isFalse();

        List<Packaging> newSavedList = repository.findAll();

        Packaging Pkg = newSavedList.getLast();

        assertThat(Pkg.getPackagingName()).isEqualTo("Something");
        assertThat(Pkg.getPackagingPrice()).isEqualTo(new BigDecimal("10000"));


    }
}