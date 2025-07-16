package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.MemberGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberGradeServiceImpl implements MemberGradeService {
    private final OrderRepository orderRepository;
    private final UserApiClient userApiClient;

    @Override
    public int sendMonthlyNetOrderAmount() {
        List<MemberGradeUpdateRequest> reqs = orderRepository.buildMemberGradeUpdateRequest();
        return userApiClient.sendNetOrderAmount(reqs);
    }
}
