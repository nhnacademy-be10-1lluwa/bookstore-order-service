package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OrderService 통합 테스트 (OrderServiceTest 전용)
 *  - 실제 DB 사용 (Replace.NONE)
 *  - profile : db
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        repository.save(createTestOrder(1L, OrderStatus.Pending));
        repository.save(createTestOrder(1L, OrderStatus.Shipped));
        repository.save(createTestOrder(3L, OrderStatus.Shipped));
        repository.save(createTestOrder(2L, OrderStatus.Confirmed));
    }

    /** Order NOT‑NULL 필드를 모두 채우는 헬퍼 */
    private Order createTestOrder(long memberId, OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                .memberId(memberId)
                .guestId(null)
                .shippingFee(BigDecimal.ZERO)
                .shippingPolicy(null)
                .orderDate(now)
                .deliveryDate(now.plusDays(3))
                .totalPrice(BigDecimal.ZERO)
                .discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .finalPrice(BigDecimal.ZERO)
                .orderStatus(status)
                .recipientName("tester")
                .recipientContact("01012345678")
                .readAddress("Seoul")
                .detailAddress("Gangnam")
                .memberCouponId(0L)
                .build();
    }

    @Test
    @DisplayName("전체 주문 조회")
    void testGetAllOrders() {
        List<OrderListResponseDto> list = service.getAllOrders();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders", Integer.class);
        assertThat(list).hasSize(count);
    }

    @Test
    @DisplayName("주문 ID로 조회")
    void testGetOrderById() {
        Order saved = repository.findAll().getFirst();
        assertThat(service.getOrderById(String.valueOf(saved.getOrderId())))
                .extracting("orderId")
                .isEqualTo(saved.getOrderId());
    }

    @Test
    @DisplayName("주문 ID NotFoundException")
    void testNotFoundException() {
        Long orderId = 10L;

        Assertions.assertThrows(NotFoundException.class,
                () -> service.getOrderById(String.valueOf(orderId)));
    }

    @Test
    @DisplayName("회원 ID로 주문 조회")
    void testGetOrderByMemberId() {
        List<OrderListResponseDto> list = service.getOrderByMemberId("1");
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE member_id = 1", Integer.class);
        assertThat(list).hasSize(cnt);
    }

    @Test
    @DisplayName("주문 상태로 조회")
    void testGetOrderByStatus() {
        List<OrderListResponseDto> list = service.getOrderByOrderStatus(OrderStatus.Shipped);
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE order_status = 'Shipped'", Integer.class);
        assertThat(list).hasSize(cnt);
    }

    @Test
    @DisplayName("주문 취소")
    void testCancelOrder() {
        Order target = repository.findAll().getFirst();
        service.cancelOrderById(String.valueOf(target.getOrderId()));
        String status = jdbcTemplate.queryForObject(
                "SELECT order_status FROM orders WHERE order_id = " + target.getOrderId(),
                String.class);
        assertThat(status).isEqualTo(OrderStatus.Cancelled.name());
    }

    @Test
    @DisplayName("주문 상태 업데이트")
    void testUpdateStatus() {
        Order target = repository.findAll().getFirst();
        service.updateOrderStatus(
                String.valueOf(target.getOrderId()),
                new OrderUpdateStatusDto(OrderStatus.Shipped));
        String status = jdbcTemplate.queryForObject(
                "SELECT order_status FROM orders WHERE order_id = " + target.getOrderId(),
                String.class);
        assertThat(status).isEqualTo(OrderStatus.Shipped.name());
    }
}
