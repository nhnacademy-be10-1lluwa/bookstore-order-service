package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderQuerydslRepository {

    // 주문 ID로 주문 내역 조회
    Optional<Order> findByOrderId(long orderId);

    // 회원별 주문 내역 조회
    List<Order> findByMemberId(Long memberId);

    // 주문 상태별 조회
    List<Order> findByOrderStatus(OrderStatus status);

    // 주문일 기준 기간 조회 (YYYY-MM-DD HH:MM:SS 기준)
    List<Order> findByOrderDateBetween(ZonedDateTime from, ZonedDateTime to);

    // 모든 주문 내역 조회
    // findAll

    @Query("update Order o set o.orderStatus = :orderStatus where o.orderId = :orderId")
    @Modifying
    @Transactional
    void updateOrderStatusByOrderId(long orderId, OrderStatus orderStatus);

}
