package com.nhnacademy.illuwa.domain.order.service.admin;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import com.nhnacademy.illuwa.domain.order.service.BookInventoryService;
import com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AdminOrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ShippingPolicyRepository shippingPolicyRepository;

    @Autowired
    PackagingRepository packagingRepository;

    @Autowired
    private BookInventoryService bookInventoryService;

    private final Long MEMBERID1 = 9998L;
    private final Long MEMBERID2 = 9999L;

    private Long generatedOrderId1;
    private String generatedOrderNumber1;


    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();
        shippingPolicyRepository.deleteAll();

        ShippingPolicy shippingPolicy = shippingPolicyRepository.save(
                ShippingPolicy.builder()
                        .active(true)
                        .fee(BigDecimal.valueOf(3000))
                        .minAmount(BigDecimal.valueOf(20000))
                        .build()
        );

        Packaging packaging = packagingRepository.save(
                Packaging.builder()
                        .packagingPrice(BigDecimal.ZERO)
                        .active(true)
                        .packagingName("포장 없음")
                        .build()
        );

        Order order = orderRepository.save(Order.builder()
                .orderNumber(NumberGenerator.generateOrderNumber(LocalDateTime.now()))
                .memberId(MEMBERID1)
                .shippingFee(BigDecimal.valueOf(3000))
                .shippingPolicy(shippingPolicy)
                .orderDate(LocalDateTime.now())
                .deliveryDate(LocalDate.now().plusDays(1))
                .totalPrice(BigDecimal.valueOf(75000))
                .discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .finalPrice(BigDecimal.valueOf(75000))
                .orderStatus(OrderStatus.Pending)
                .recipientName("tom")
                .recipientContact("010-1111-1111")
                .postCode("123456")
                .readAddress("대한민국 행복시 대단대로 101")
                .detailAddress("휼룡APT 101동 101호")
                .build());

        generatedOrderId1 = order.getOrderId();
        generatedOrderNumber1 = order.getOrderNumber();

        Order order2 = orderRepository.save(Order.builder()
                .orderNumber(NumberGenerator.generateOrderNumber(LocalDateTime.now()))
                .memberId(MEMBERID2)
                .shippingFee(BigDecimal.valueOf(3000))
                .shippingPolicy(shippingPolicy)
                .orderDate(LocalDateTime.now())
                .deliveryDate(LocalDate.now().plusDays(1))
                .totalPrice(BigDecimal.valueOf(75000))
                .discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .finalPrice(BigDecimal.valueOf(75000))
                .orderStatus(OrderStatus.Pending)
                .recipientName("tom")
                .recipientContact("010-1111-1111")
                .postCode("123456")
                .readAddress("대한민국 행복시 대단대로 101")
                .detailAddress("휼룡APT 101동 101호")
                .build());

        orderItemRepository.save(OrderItem.builder()
                .bookId(9999L)
                .order(order)
                .quantity(3)
                .price(BigDecimal.valueOf(25000))
                .discountPrice(BigDecimal.ZERO)
                .itemTotalPrice(BigDecimal.valueOf(75000))
                .packaging(packaging)
                .build());

        orderItemRepository.save(OrderItem.builder()
                .bookId(9999L)
                .order(order2)
                .quantity(3)
                .price(BigDecimal.valueOf(25000))
                .discountPrice(BigDecimal.ZERO)
                .itemTotalPrice(BigDecimal.valueOf(75000))
                .packaging(packaging)
                .build());
    }

    @Test
    @DisplayName("어드민 전체 주문 내역 조회")
    void getOrderListAllTest() {
        Page<OrderListResponseDto> result = adminOrderService.getAllOrders(PageRequest.of(0, 10));
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("주문 번호로 주문 조회")
    void getOrderByOrderNumberTest() {
        String num = generatedOrderNumber1;

        OrderResponseDto dto = adminOrderService.getOrderByNumber(num);

        assertThat(dto.getOrderId()).isEqualTo(generatedOrderId1);
    }

    @Test
    @DisplayName("멤버 ID로 주문내역 조회")
    void getOrdersByMemberId() {
        Page<OrderListResponseDto> result1 = adminOrderService
                .getOrderByMemberId(MEMBERID1, PageRequest.of(0, 10));
        Page<OrderListResponseDto> result2 = adminOrderService
                .getOrderByMemberId(MEMBERID2, PageRequest.of(0, 10));

        assertThat(result1).hasSize(1);
        assertThat(result2).hasSize(1);
    }

    @Test
    @DisplayName("주문 상태 별 주문내역 조회")
    void getOrdersByOrderStatus() {
        Page<OrderListResponseDto> result = adminOrderService
                .getOrderByOrderStatus(OrderStatus.Pending, PageRequest.of(0, 10));

        assertThat(result).hasSize(2);
    }
}
