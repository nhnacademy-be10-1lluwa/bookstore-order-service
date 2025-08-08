package com.nhnacademy.illuwa.domain.order.service.admin.impl;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUtilsServiceImpl implements AdminUtilsService {
    private final OrderRepository orderRepository;
    private final UserApiClient userApiClient;


    @Override
    public int sendMonthlyNetOrderAmount() {
        List<MemberGradeUpdateRequest> reqs = orderRepository.buildMemberGradeUpdateRequest();
        String idempotencyKey = UUID.randomUUID().toString();
        return userApiClient.sendNetOrderAmount(idempotencyKey, reqs);
    }

    @Override
    public Map<String, Integer> dbDataScheduler() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        int deleteOrderItem = orderRepository.deleteItemsBefore(OrderStatus.AwaitingPayment, threeDaysAgo);
        int deleteOrder = orderRepository.deleteByOrderStatusAndOrderDateBefore(OrderStatus.AwaitingPayment, threeDaysAgo);

        Map<String, Integer> map = new HashMap<>();
        map.put("deleteOrderItem", deleteOrderItem);
        map.put("deleteOrder", deleteOrder);
        return map;
    }
}
