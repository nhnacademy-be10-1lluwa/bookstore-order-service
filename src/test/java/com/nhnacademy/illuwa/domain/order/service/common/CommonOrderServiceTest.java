package com.nhnacademy.illuwa.domain.order.service.common;


import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.PointRequest;
import com.nhnacademy.illuwa.common.external.user.dto.TotalRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CommonOrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CommonOrderService commonOrderService;

    @Autowired
    private ShippingPolicyRepository shippingPolicyRepository;

    @Autowired
    private PackagingRepository packagingRepository;

    @MockBean
    private UserApiClient userApiClient;

    @MockBean
    private ProductApiClient productApiClient;

    private final Long ORDER_ID_1 = 80001L;
    private final Long ORDER_ID_2 = 80002L;
    private final Long MEMBER_ID_1 = 70001L;
    private final Long MEMBER_ID_2 = 70002L;

    private ShippingPolicy shippingPolicy;
    private Packaging defaultPackaging;

    @BeforeEach
    void setUpFixtures() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        shippingPolicyRepository.deleteAll();
        packagingRepository.deleteAll();

        shippingPolicy = shippingPolicyRepository.save(
                ShippingPolicy.builder()
                        .active(true)
                        .fee(BigDecimal.valueOf(3000))
                        .minAmount(BigDecimal.valueOf(20000))
                        .build()
        );

        defaultPackaging = packagingRepository.save(
                Packaging.builder()
                        .packagingName("포장 없음")
                        .packagingPrice(BigDecimal.ZERO)
                        .active(true)
                        .build()
        );
    }

    private Order createOrder(Long orderId, Long memberId, OrderStatus status, BigDecimal usedPoint, LocalDate deliveryDate, BigDecimal totalPrice) {
        Order order = orderRepository.save(Order.builder()
                .orderId(orderId)
                .orderNumber("ORD-" + orderId)
                .memberId(memberId)
                .shippingFee(BigDecimal.valueOf(3000))
                .shippingPolicy(shippingPolicy)
                .orderDate(LocalDateTime.now())
                .deliveryDate(deliveryDate)
                .totalPrice(totalPrice)
                .discountPrice(BigDecimal.ZERO)
                .usedPoint(usedPoint)
                .finalPrice(totalPrice)
                .orderStatus(status)
                .recipientName("tester")
                .recipientContact("010-0000-0000")
                .postCode("12345")
                .readAddress("주소")
                .detailAddress("상세주소")
                .build());

        orderItemRepository.save(OrderItem.builder()
                .bookId(9000L)
                .order(order)
                .quantity(2)
                .price(totalPrice.divide(BigDecimal.valueOf(2)))
                .discountPrice(BigDecimal.ZERO)
                .itemTotalPrice(totalPrice)
                .packaging(defaultPackaging)
                .build());

        return order;
    }

    @Test
    @DisplayName("updateOrderPaymentByOrderNumber: member가 있으면 사용 포인트를 전송한다")
    void updateOrderPaymentByOrderNumber_sendsUsedPoint() {
        // given
        BigDecimal usedPoint = BigDecimal.valueOf(1234);
        Order order = createOrder(ORDER_ID_2, MEMBER_ID_2, OrderStatus.Pending, usedPoint, LocalDate.now(), BigDecimal.valueOf(75000));


        // when
        commonOrderService.updateOrderPaymentByOrderNumber(order.getOrderNumber());

        // then
        ArgumentCaptor<PointRequest> captor = ArgumentCaptor.forClass(PointRequest.class);
        verify(userApiClient).sendUsedPointByMemberId(captor.capture());
        assertThat(captor.getValue().getMemberId()).isEqualTo(MEMBER_ID_2);
        assertThat(captor.getValue().getUsedPoint()).isEqualByComparingTo(usedPoint);
    }

    @Test
    @DisplayName("orderCancel: 주문과 아이템이 모두 삭제된다")
    void orderCancel_deletesOrderAndItems() {
        // given
        Order order = createOrder(ORDER_ID_1, MEMBER_ID_1, OrderStatus.Pending, BigDecimal.ZERO, LocalDate.now(), BigDecimal.valueOf(30000));

        // when
        commonOrderService.orderCancel(order.getOrderId());

        // then
        assertThat(orderRepository.findByOrderId(order.getOrderId())).isEmpty();
        assertThat(orderItemRepository.findOrderItemDtosByOrderId(order.getOrderId())).isEmpty();
    }

    @Test
    @DisplayName("refundOrderById: 파손/파본 사유일 때 총액+포인트+배송비 환불, 상태 Returned")
    void refundOrderById_damaged_fullRefund() {
        // given
        Order order = createOrder(ORDER_ID_1, MEMBER_ID_1, OrderStatus.Delivered, BigDecimal.valueOf(1000), LocalDate.now().minusDays(5), BigDecimal.valueOf(50000));

        ReturnRequestCreateRequestDto dto = mock(ReturnRequestCreateRequestDto.class);
        when(dto.getReason()).thenReturn(ReturnReason.Item_Damaged);

        doNothing().when(userApiClient).sendReturnPrice(any(TotalRequest.class));
        doNothing().when(productApiClient).sendRestoreBooksCount(anyList());

        // when
        OrderResponseDto response = commonOrderService.refundOrderById(order.getOrderId(), dto);

        // then
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.Returned);

        ArgumentCaptor<TotalRequest> totalCaptor = ArgumentCaptor.forClass(TotalRequest.class);
        verify(userApiClient).sendReturnPrice(totalCaptor.capture());

        BigDecimal expected = BigDecimal.valueOf(50000).add(BigDecimal.valueOf(1000)).add(BigDecimal.valueOf(3000));
        assertThat(totalCaptor.getValue().getPrice()).isEqualByComparingTo(expected);

        verify(productApiClient).sendRestoreBooksCount(anyList());
    }

    @Test
    @DisplayName("refundOrderById: 10일 이내 반품(파손 아님) → 총액+포인트 환불, 상태 Returned")
    void refundOrderById_within10days_normalReason() {
        // given
        Order order = createOrder(ORDER_ID_2, MEMBER_ID_2, OrderStatus.Delivered, BigDecimal.valueOf(500), LocalDate.now().minusDays(3), BigDecimal.valueOf(20000));

        ReturnRequestCreateRequestDto dto = mock(ReturnRequestCreateRequestDto.class);
        when(dto.getReason()).thenReturn(ReturnReason.Change_Mind); // 파손/파본 아님

        doNothing().when(userApiClient).sendReturnPrice(any(TotalRequest.class));
        doNothing().when(productApiClient).sendRestoreBooksCount(anyList());

        // when
        OrderResponseDto response = commonOrderService.refundOrderById(order.getOrderId(), dto);

        // then
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.Returned);

        ArgumentCaptor<TotalRequest> totalCaptor = ArgumentCaptor.forClass(TotalRequest.class);
        verify(userApiClient).sendReturnPrice(totalCaptor.capture());

        BigDecimal expected = BigDecimal.valueOf(20000).add(BigDecimal.valueOf(500));
        assertThat(totalCaptor.getValue().getPrice()).isEqualByComparingTo(expected);
    }

    @Test
    @DisplayName("refundOrderById: 30일 초과 시 예외 발생")
    void refundOrderById_over30days_throws() {
        // given
        Order order = createOrder(ORDER_ID_1, MEMBER_ID_1, OrderStatus.Delivered, BigDecimal.ZERO, LocalDate.now().minusDays(31), BigDecimal.valueOf(15000));

        ReturnRequestCreateRequestDto dto = mock(ReturnRequestCreateRequestDto.class);
        when(dto.getReason()).thenReturn(ReturnReason.Change_Mind);

        // expect
        assertThatThrownBy(() -> commonOrderService.refundOrderById(order.getOrderId(), dto))
                .isInstanceOf(com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException.class)
                .hasMessageContaining("반품 기한");
    }
}
