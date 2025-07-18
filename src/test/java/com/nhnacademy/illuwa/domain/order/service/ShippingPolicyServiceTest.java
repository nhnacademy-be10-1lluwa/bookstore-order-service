package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.OrderServiceApplication;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.AllShippingPolicyDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.shippingPolicy.ShippingPolicyResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
public class ShippingPolicyServiceTest {

    @Autowired
    private ShippingPolicyService service;

    @Autowired
    private ShippingPolicyRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void setUp() {

        repository.save(ShippingPolicy.builder()
                .minAmount(new BigDecimal("30000"))
                .fee(new BigDecimal("5000"))
                .active(true)
                .build()
                );

        repository.save(ShippingPolicy.builder()
                .minAmount(new BigDecimal("20000"))
                .fee(new BigDecimal("3000"))
                .active(true)
                .build());

        repository.save(ShippingPolicy.builder()
                .minAmount(new BigDecimal("25000"))
                .fee(new BigDecimal("4000"))
                .active(false)
                .build());
    }

    @Test
    @DisplayName("전체 조회 테스트")
    void testAllShippingPolicy() {
        List<AllShippingPolicyDto> dtos = service.getAllShippingPolicy();

        assertThat(dtos).extracting("fee")
                .contains(new BigDecimal("5000.00"), new BigDecimal("3000.00"), new BigDecimal("4000.00"));

    }

    @Test
    @DisplayName("활성 정책 조회 테스트")
    @Disabled
    void testGetShippingPolicyByActive() {
        ShippingPolicyResponseDto dto = service.getShippingPolicyByActive(true);

        Integer activeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM shipping_policies WHERE active = true", Integer.class
        );

        /*assertThat(dto).hasSize(activeCount);*/
    }


    @Test
    @DisplayName("정책 주가 및 조회")
    void testAdd_and_getShippingPolicy() {
        ShippingPolicyCreateRequestDto req = new ShippingPolicyCreateRequestDto(new BigDecimal("10000"), new BigDecimal("5000"));
        ShippingPolicyResponseDto created = service.addShippingPolicy(req);

        ShippingPolicyResponseDto dto = service.getShippingPolicy(created.getShippingPolicyId());
        assertThat(dto.getMinAmount()).isEqualTo(new BigDecimal("10000.00"));
        assertThat(dto.getFee()).isEqualTo(new BigDecimal("5000.00"));

    }

    @Test
    @DisplayName("포장 옵션 비활성화")
    void testRemoveShippingPolicy() {

        List<ShippingPolicy> savedList = repository.findAll();

        Long targetId = savedList.getLast().getShippingPolicyId();

        int removed = service.removeShippingPolicy(targetId);

        assertThat(removed).isEqualTo(1);
        Boolean isActive = jdbcTemplate.queryForObject(
                String.format("SELECT active FROM shipping_policies WHERE shipping_policy_id = %d", targetId), Boolean.class
        );
        assertThat(isActive).isFalse();
    }

}
