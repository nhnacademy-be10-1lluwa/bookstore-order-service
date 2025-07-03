/*
package com.nhnacademy.illuwa.domain.order.service;


import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.*;
import com.nhnacademy.illuwa.domain.order.entity.*;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ReturnRequestRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
@Disabled
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
    private PackagingRepository packagingRepository;

    @Autowired
    private ShippingPolicyRepository shippingPolicyRepository;


    private ShippingPolicy shippingPolicy;
    private Packaging packaging;

    private Order order4;

    @BeforeEach
    void setUp() {

        returnRepository.deleteAll();
        orderRepository.deleteAll();
        shippingPolicyRepository.deleteAll();
        packagingRepository.deleteAll();

        shippingPolicy = shippingPolicyRepository.save(ShippingPolicy.builder()
                .minAmount(new BigDecimal("30000"))
                .fee(new BigDecimal("5000"))
                .active(true)
                .build()
        );

        packaging = packagingRepository.save(Packaging.builder()
                .packagingName("Box")
                .packagingPrice(new BigDecimal("1000"))
                .active(true)
                .build());

        List<OrderItemRequestDto> items = new ArrayList<>();
        items.add(new OrderItemRequestDto(1L, 2, 1L, packaging.getPackagingId()));
        items.add(new OrderItemRequestDto(2L, 1, null, packaging.getPackagingId()));

        Order order1 = orderService.createOrderWithItems(new OrderCreateRequestDto(1L, null,
                shippingPolicy.getShippingPolicyId(), items, LocalDateTime.now()));

        Order order2 = orderService.createOrderWithItems(new OrderCreateRequestDto(2L, null,
                shippingPolicy.getShippingPolicyId(), items, LocalDateTime.now()));

        Order order3 = orderService.createOrderWithItems(new OrderCreateRequestDto(3L, null,
                shippingPolicy.getShippingPolicyId(), items, LocalDateTime.now()));

        order4 = orderService.createOrderWithItems(new OrderCreateRequestDto(3L, null,
                shippingPolicy.getShippingPolicyId(), items, LocalDateTime.now()));


        returnRequestService.addReturnRequest(parseId(order1.getOrderId()), new ReturnRequestCreateRequestDto(LocalDateTime.now(), ReturnReason.Change_Mind));
        returnRequestService.addReturnRequest(parseId(order2.getOrderId()), new ReturnRequestCreateRequestDto(LocalDateTime.now(), ReturnReason.Item_Damaged));
        returnRequestService.addReturnRequest(parseId(order3.getOrderId()), new ReturnRequestCreateRequestDto(LocalDateTime.now(), ReturnReason.Other));
    }

    @Test
    @DisplayName("반품 내역 전체 조회 테스트")
    void testAllReturnRequest() {
        List<ReturnRequestListResponseDto> dtos = returnRequestService.getAllReturnRequest();

        assertThat(dtos.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("반품 요청 추가 및 조회")
    void testAll_and_getReturnRequest() {
        ReturnRequestCreateRequestDto req = new ReturnRequestCreateRequestDto(LocalDateTime.now(), ReturnReason.Defective_Item);

        ReturnRequest created = returnRequestService.addReturnRequest(parseId(order4.getOrderId()), req);

        ReturnRequestResponseDto dto =  returnRequestService.getReturnRequest(parseId(created.getReturnId()));

        assertThat(dto.getReturnedAt()).isEqualTo(created.getReturnedAt());
        assertThat(dto.getStatus()).isEqualTo(created.getStatus());
    }

    @Test
    @DisplayName("반품 요청 취소")
    void testRemoveReturnRequest() {
        List<ReturnRequestListResponseDto> dto = returnRequestService.getAllReturnRequest();
        returnRequestService.removeReturnRequest(parseId(dto.getLast().getReturnId()));


        assertThat(dto.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("환불 상태 변경")
    void testReturnRequestStatus() {
        List<ReturnRequestListResponseDto> dto = returnRequestService.getAllReturnRequest();

        Long targetId = dto.getLast().getReturnId();

        returnRequestService.updateReturnRequest(targetId, new AdminReturnRequestRegisterDto(ReturnStatus.Approved));

        ReturnRequestResponseDto updated = returnRequestService.getReturnRequest(targetId.toString());

        assertThat(updated.getStatus()).isEqualTo(ReturnStatus.Approved);
    }



    public String parseId(Long id) {
        return id.toString();
    }

}
*/
