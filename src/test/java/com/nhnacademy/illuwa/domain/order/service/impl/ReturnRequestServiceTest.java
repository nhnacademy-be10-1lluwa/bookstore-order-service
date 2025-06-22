package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ReturnRequestRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import com.nhnacademy.illuwa.domain.order.service.ReturnRequestService;
import com.nhnacademy.illuwa.domain.order.service.ShippingPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
public class ReturnRequestServiceTest {

    @Autowired
    private ReturnRequestService returnRequestService;

    @Autowired
    private ReturnRequestRepository returnRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingPolicyRepository shippingPolicyRepository;

    @Autowired
    private PackagingRepository packagingRepository;

    @BeforeEach
    void setUp() {

        returnRepository.deleteAll();
        orderRepository.deleteAll();

        ShippingPolicy shippingPolicy = shippingPolicyRepository.save(ShippingPolicy.builder()
                .minAmount(new BigDecimal("30000"))
                .fee(new BigDecimal("5000"))
                .active(true)
                .build()
        );

        Packaging packaging = packagingRepository.save(Packaging.builder()
                .packagingName("Box")
                .packagingPrice(new BigDecimal("1000"))
                .active(true)
                .build());

        List<OrderItemRequestDto> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemRequestDto(1L, 2, 1L, packaging.getPackagingId()));
        orderItems.add(new OrderItemRequestDto(2L, 1, null, packaging.getPackagingId()));

        orderService.createOrderWithItems(new OrderCreateRequestDto(1L, null, shippingPolicy.getShippingPolicyId(), orderItems, LocalDateTime.now()
        ));

        orderService.createOrderWithItems(new OrderCreateRequestDto(2L, null, shippingPolicy.getShippingPolicyId(), orderItems, LocalDateTime.now()
        ));

        orderService.createOrderWithItems(new OrderCreateRequestDto(3L, null, shippingPolicy.getShippingPolicyId(), orderItems, LocalDateTime.now()
        ));
    }

    @Test
    @DisplayName("주문 전체 조회 테스트")
    void testAllOrder() {
        List<OrderListResponseDto> dtos = orderService.getAllOrders();


        System.out.println(dtos.stream().toList());
    }
}
