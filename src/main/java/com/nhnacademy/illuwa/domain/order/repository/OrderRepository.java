package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderQuerydslRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderQuerydslRepository {

    // 주문 ID로 주문 내역 조회
    Optional<Order> findByOrderId(Long orderId);

    // 주문 번호로 주문내역 조회하기
    Optional<Order> findByOrderNumber(String orderNumber);

    // 회원별 주문 내역 조회
    Page<Order> findByMemberId(Long memberId, Pageable pageable);

    // 주문 상태별 조회
    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);

    // 주문일 기준 기간 조회 (YYYY-MM-DD HH:MM:SS 기준)
    Page<Order> findByOrderDateBetween(ZonedDateTime from, ZonedDateTime to, Pageable pageable);

    // 모든 주문 내역 조회
    // findAll

    // 최신 주문 조회
    Optional<Order> findTopByOrderByOrderIdDesc();

    @Query("update Order o set o.orderStatus = :orderStatus where o.orderId = :orderId")
    @Modifying
    @Transactional
    void updateOrderStatusByOrderId(long orderId, OrderStatus orderStatus);

    // 3일 동안 AwaitingPayment 에 머물러 있는 order item 데이터 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order.orderId IN (" +
            "  SELECT o.orderId FROM Order o WHERE o.orderStatus = :status AND o.orderDate < :threshold" +
            ")")
    int deleteItemsBefore(@Param("status") OrderStatus status,
                          @Param("threshold")LocalDateTime threshold);

    // 3일동안 AwaitingPayment 에 머물러 있는 order 데이터 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Order o " +
            "WHERE o.orderStatus = :status " +
            "AND o.orderDate < :threshold")
    int deleteByOrderStatusAndOrderDateBefore(
            @Param("status") OrderStatus status,
            @Param("threshold")LocalDateTime threshold);

    Long orderId(long orderId);
}
