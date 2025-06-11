package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 회원별 주문 내역 조회
    List<Order> findByMemberId(Long memberId);

    // 주문 상태별 조회
    List<Order> findByOrderStatus(OrderStatus status);

    // 주문일 기준 기간 조회 (YYYY-MM-DD HH:MM:SS 기준)
    List<Order> findByOrderDateBetween(ZonedDateTime from, ZonedDateTime to);

}
