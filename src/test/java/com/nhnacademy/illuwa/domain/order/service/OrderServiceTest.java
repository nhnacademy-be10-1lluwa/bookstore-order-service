/*
package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("db")
@Transactional
@Disabled
public class OrderServiceTest {



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

    */
/** Order NOT‑NULL 필드를 모두 채우는 헬퍼 *//*

    private Order createTestOrder(long memberId, OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8))
                .memberId(memberId)
                .guestId(null)
                .shippingFee(BigDecimal.ZERO)
                .shippingPolicy(new ShippingPolicy())
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
                .detailAddress("Gangnam")
                .memberCouponId(0L)
                .postCode("222222")
                .build();
    }

    @Test
    @DisplayName("전체 주문 조회")
    void testGetAllOrders() {
        Page<OrderListResponseDto> page = service.getAllOrders(Pageable.unpaged());
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders", Integer.class);

        assert count != null;
        assertThat(page.getTotalElements()).isEqualTo(count.longValue());
        assertThat(page.getContent()).hasSize(count);
    }

    @Test
    @DisplayName("주문 ID로 조회")
    void testGetOrderById() {
        Order saved = repository.findAll().getFirst();
        assertThat(service.getOrderByOrderId(saved.getOrderId()))
                .extracting("orderId")
                .isEqualTo(saved.getOrderId());
    }

    @Test
    @DisplayName("주문 ID NotFoundException")
    void testNotFoundException() {
//        Long orderId = 10L;

        long orderId = repository.findAll().stream()
                .mapToLong(Order::getOrderId)
                .max()
                .orElse(0L) + 10;

        Assertions.assertThrows(NotFoundException.class,
                () -> service.getOrderByOrderId(orderId));
    }

    @Test
    @DisplayName("회원 ID로 주문 조회")
    void testGetOrderByMemberId() {
        Page<OrderListResponseDto> page = service.getOrderByMemberId(1L, Pageable.unpaged());
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE member_id = 1", Integer.class);
        assert cnt != null;
        assertThat(page.getTotalElements()).isEqualTo(cnt.longValue());
        assertThat(page.getContent()).hasSize(cnt);
    }

    @Test
    @DisplayName("주문 상태로 조회")
    void testGetOrderByStatus() {
        Page<OrderListResponseDto> page = service.getOrderByOrderStatus(OrderStatus.Shipped, Pageable.unpaged());
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE order_status = 'Shipped'", Integer.class);
        assert cnt != null;
        assertThat(page.getTotalElements()).isEqualTo(cnt.longValue());
        assertThat(page.getContent()).hasSize(cnt);
    }

    @Test
    @DisplayName("주문 취소")
    void testCancelOrder() {
        Order target = repository.findAll().getFirst();
        service.cancelOrderByOrderNumber(target.getOrderNumber());

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
                target.getOrderId(),
                new OrderUpdateStatusDto(OrderStatus.Shipped));
        String status = jdbcTemplate.queryForObject(
                "SELECT order_status FROM orders WHERE order_id = " + target.getOrderId(),
                String.class);
        assertThat(status).isEqualTo(OrderStatus.Shipped.name());
    }
}
*/
