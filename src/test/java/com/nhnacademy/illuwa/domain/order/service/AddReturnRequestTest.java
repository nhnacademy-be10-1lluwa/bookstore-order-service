/*
package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ReturnRequest;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ReturnRequestRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
@Disabled
public class AddReturnRequestTest {

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

    private ShippingPolicy shippingPolicy;
    private Packaging packaging;


    @BeforeEach
    void setUp() {

        returnRepository.deleteAll();
        orderRepository.deleteAll();

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

    }

    // 배송일로 주문을 1건 생성하고
    // DB에 실제로 저장된 orderId(String)를 반환
    private String createOrder(LocalDateTime deliveryDate) {
        List<OrderItemRequestDto> items = new ArrayList<>();
        items.add(new OrderItemRequestDto(1L, 2, 1L, packaging.getPackagingId())); // bookId : 1, 수량 : 2
        items.add(new OrderItemRequestDto(2L, 1, null, packaging.getPackagingId())); // bookId : 2, 수량 : 1

        orderService.createOrderWithItems(
                new OrderCreateRequestDto(
                        1L,
                        null,
                        shippingPolicy.getShippingPolicyId(),
                        items,
                        deliveryDate
                )
        );

        Order saved = orderRepository.findTopByOrderByOrderIdDesc().orElseThrow();

        return String.valueOf(saved.getOrderId());
    }


    @Test
    @DisplayName("출고 10일 이내 미사용 사유 -> 반품 요청 성공")
    void addReturnRequest_within10Days_success() {
        String orderId = createOrder(LocalDateTime.now());

        ReturnRequestCreateRequestDto dto = new ReturnRequestCreateRequestDto(
                LocalDateTime.now(),
                ReturnReason.Change_Mind
        );

        ReturnRequest saved = returnRequestService.addReturnRequest(orderId, dto);

        assertNotNull(String.valueOf(saved.getReturnId()), "반품 ID가 생성되어야 합니다.");
        assertEquals("해당 반품 테이블의 상태가 일치해야 합니다.", ReturnStatus.Requested, saved.getStatus());
        assertEquals("해당 주문의 상태가 일치해야 합니다.", OrderStatus.Returned, saved.getOrder().getOrderStatus());
    }

    @Test
    @DisplayName("출고 10일 이상 사용 -> 반품 요청 실패")
    void addReturnRequest_within10Days_fail() {
        String orderId = createOrder(LocalDateTime.now());

        ReturnRequestCreateRequestDto dto = new ReturnRequestCreateRequestDto(
                LocalDateTime.now().minusDays(15),
                ReturnReason.Change_Mind
        );

        ReturnRequest saved = returnRequestService.addReturnRequest(orderId, dto);

        Assertions.assertThrows(BadRequestException.class, () ->
                returnRequestService.addReturnRequest(orderId, dto));

        assertNotNull(String.valueOf(saved.getReturnId()), "반품 ID가 생성되어야 합니다.");
    }

    @Test
    @DisplayName("출고 30일 이내 상품 파손 반품 -> 반품 요청 성공")
    void addReturnRequest_within30Days_success() {
        String orderId = createOrder(LocalDateTime.now());

        ReturnRequestCreateRequestDto dto = new ReturnRequestCreateRequestDto(
                LocalDateTime.now().minusDays(20),
                ReturnReason.Defective_Item
        );

        ReturnRequest saved = returnRequestService.addReturnRequest(orderId, dto);

        assertNotNull(String.valueOf(saved.getReturnId()), "반품 ID가 생성되어야 합니다.");
        assertEquals("해당 반품 테이블의 상태가 일치해야 합니다.", ReturnStatus.Requested, saved.getStatus());
        assertEquals("해당 주문의 상태가 일치해야 합니다.", OrderStatus.Returned, saved.getOrder().getOrderStatus());
    }

    @Test
    @DisplayName("출고 30일 이상 상품 파손 -> 반품 요청 실패")
    void addReturnRequest_within30Days_fail() {
        String orderId = createOrder(LocalDateTime.now());

        ReturnRequestCreateRequestDto dto = new ReturnRequestCreateRequestDto(
                LocalDateTime.now().minusDays(31),
                ReturnReason.Change_Mind
        );

        ReturnRequest saved = returnRequestService.addReturnRequest(orderId, dto);

        Assertions.assertThrows(BadRequestException.class, () ->
                returnRequestService.addReturnRequest(orderId, dto));

        assertNotNull(String.valueOf(saved.getReturnId()), "반품 ID가 생성되어야 합니다.");
    }

}
*/
