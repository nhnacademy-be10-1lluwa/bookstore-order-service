package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 주문별 아이템 조회
    List<OrderItem> findByOrderOrderId(Long OrderId);


}
