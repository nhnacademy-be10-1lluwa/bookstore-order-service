package com.nhnacademy.illuwa.domain.order.repository;

import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.repository.custom.OrderItemQuerydslRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemQuerydslRepository {

    // 주문 번호로 아이템 조회
    Optional<OrderItem> findByOrderItemId(long orderItemId);

    // 주문별 아이템 조회
    List<OrderItem> findByOrderOrderId(long OrderId);

    // 멤버별 아이템 조회
    @Query("select oi " +
            "from OrderItem oi "+
            "join oi.order o "+
            "WHERE o.memberId = :memberId"
    )
    @Transactional(readOnly = true)
    List<OrderItem> findByMemberId(long memberId);
}
