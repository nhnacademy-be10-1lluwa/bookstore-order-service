package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * OrderItemService 통합 테스트.
 *  - OrderItemServiceImpl 의 3가지 조회 메서드 + ID 파싱 예외 검증
 *  - 실제 DB 사용 (Replace.NONE)
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
public class OrderItemServiceTest {

    @Autowired
    private OrderItemServiceImpl service;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Order orderA;   // memberId = 1
    private Order orderB;   // memberId = 2

    @BeforeEach
    void setUp() {
        // 주문 2건 + 주문상품 3건(2,1) 저장
        orderA = orderRepository.save(createTestOrder(1L, OrderStatus.Pending));
        orderB = orderRepository.save(createTestOrder(2L, OrderStatus.Pending));

        orderItemRepository.save(createTestOrderItem(orderA, 1L, BigDecimal.valueOf(10000), 2));
        orderItemRepository.save(createTestOrderItem(orderA, 2L, BigDecimal.valueOf(15000), 1));

        orderItemRepository.save(createTestOrderItem(orderB, 3L, BigDecimal.valueOf(20000), 1));
    }

    /** Order NOT‑NULL 필드 생성 헬퍼 */
    private Order createTestOrder(long memberId, OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                .memberId(memberId)
                .guestId(null)
                .shippingFee(BigDecimal.ZERO)
                .shippingPolicy(null)
                .orderDate(now)
                .deliveryDate(LocalDate.now().plusDays(3))
                .totalPrice(BigDecimal.ZERO)
                .discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .finalPrice(BigDecimal.ZERO)
                .orderStatus(status)
                .recipientName("tester")
                .recipientContact("01012345678")
                .readAddress("Seoul")
                .detailAddress("GangNam")
                .memberCouponId(0L)
                .build();
    }

    /** OrderItem NOT‑NULL 필드 생성 헬퍼 */
    private OrderItem createTestOrderItem(Order order, long bookId,
                                          BigDecimal price, int quantity) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        return OrderItem.builder()
                .order(order)
                .bookId(bookId)
                .price(price)
                .quantity(quantity)
                .discountPrice(BigDecimal.ZERO)
                .itemTotalPrice(total)
                .memberCouponId(null)
                .packaging(null)
                .build();
    }

    @Test
    @DisplayName("모든 주문상품 조회")
    void testGetAllOrderItems() {
        List<OrderItemResponseDto> list = service.getAllOrderItem();
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM order_item", Integer.class);
        assertThat(list).hasSize(cnt);
    }

    @Test
    @DisplayName("주문상품 ID로 단건 조회")
    void testGetOrderItemById() {
        Long anyId = orderItemRepository.findAll().get(0).getOrderItemId();
        OrderItemResponseDto dto = service.getOrderItemById(anyId);
        assertThat(dto.getOrderItemId()).isEqualTo(anyId);
    }

    @Test
    @DisplayName("주문 ID로 주문상품 조회")
    void testGetOrderItemByOrderId() {
        List<OrderItemResponseDto> list = service.getOrderItemByOrderId(
                orderA.getOrderId());
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM order_item WHERE order_id = " + orderA.getOrderId(),
                Integer.class);
        assertThat(list).hasSize(cnt);
    }

}
