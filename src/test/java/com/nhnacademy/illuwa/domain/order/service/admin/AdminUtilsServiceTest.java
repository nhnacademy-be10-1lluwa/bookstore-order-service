package com.nhnacademy.illuwa.domain.order.service.admin;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberGradeUpdateRequest;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.admin.impl.AdminUtilsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class AdminUtilsServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    UserApiClient userApiClient;

    @InjectMocks
    AdminUtilsServiceImpl adminUtilsService;

    @Test
    @DisplayName("멤버별 3개월 간 순수 주문 금액 전송")
    void sendMonthlyNetOrderAmount_success() {
        List<MemberGradeUpdateRequest> requests = List.of(
                new MemberGradeUpdateRequest(1L, BigDecimal.valueOf(300000)),
                new MemberGradeUpdateRequest(2L, BigDecimal.valueOf(150000))
        );
        given(orderRepository.buildMemberGradeUpdateRequest()).willReturn(requests);
        given(userApiClient.sendNetOrderAmount(requests)).willReturn(3);

        // when
        int result = adminUtilsService.sendMonthlyNetOrderAmount();

        // then
        assertThat(result).isEqualTo(3);
        verify(orderRepository).buildMemberGradeUpdateRequest();
        verify(userApiClient).sendNetOrderAmount(requests);
        verifyNoMoreInteractions(orderRepository, userApiClient);
    }

    @Test
    @DisplayName("3일 간 AwaitingPayment 상태인 주문내역 삭제")
    void dbDataScheduler() {
        given(orderRepository.deleteItemsBefore(eq(OrderStatus.AwaitingPayment), any(LocalDateTime.class)))
                .willReturn(7);
        given(orderRepository.deleteByOrderStatusAndOrderDateBefore(eq(OrderStatus.AwaitingPayment), any(LocalDateTime.class)))
                .willReturn(3);

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);

        Map<String, Integer> map = adminUtilsService.dbDataScheduler();

        assertThat(map)
                .containsEntry("deleteOrderItem", 7)
                .containsEntry("deleteOrder", 3);

        verify(orderRepository).deleteItemsBefore(eq(OrderStatus.AwaitingPayment), captor.capture());
        LocalDateTime passed = captor.getValue();
        LocalDateTime nowMinus3 = LocalDateTime.now().minusDays(3);
        assertThat(passed.isBefore(nowMinus3.plusSeconds(5)) && passed.isAfter(nowMinus3.minusSeconds(5))).isTrue();

        verify(orderRepository).deleteByOrderStatusAndOrderDateBefore(eq(OrderStatus.AwaitingPayment), any(LocalDateTime.class));
        verifyNoMoreInteractions(orderRepository);

    }
}
