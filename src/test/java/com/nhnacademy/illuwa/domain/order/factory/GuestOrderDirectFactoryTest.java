package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.common.external.product.dto.OrderItemDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider.ItemPrice;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GuestOrderDirectFactoryTest {

    @Test
    void create_ShouldCreateGuestOrderCorrectly() {
        PackagingRepository packagingRepository = mock(PackagingRepository.class);
        ShippingPolicyRepository shippingPolicyRepository = mock(ShippingPolicyRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        ItemPriceProvider priceProvider = mock(ItemPriceProvider.class);
        DiscountCalculator discountCalculator = mock(DiscountCalculator.class);
        MemberCouponService memberCouponService = mock(MemberCouponService.class);

        GuestOrderDirectFactory factory = new GuestOrderDirectFactory(
                packagingRepository,
                shippingPolicyRepository,
                orderRepository,
                priceProvider,
                discountCalculator,
                memberCouponService
        );

        Packaging packaging = Packaging.builder().packagingPrice(BigDecimal.valueOf(500)).build();
        ShippingPolicy shippingPolicy = ShippingPolicy.builder()
                .fee(BigDecimal.valueOf(3000))
                .minAmount(BigDecimal.valueOf(20000))
                .active(true)
                .build();

        when(packagingRepository.findByPackagingId(1L)).thenReturn(Optional.of(packaging));
        when(shippingPolicyRepository.findByActive(true)).thenReturn(Optional.of(shippingPolicy));

        when(priceProvider.fetchPrice(eq(1L), eq(3), eq(null), any()))
                .thenReturn(new ItemPrice(BigDecimal.valueOf(10000), BigDecimal.valueOf(30000), BigDecimal.ZERO));

        OrderItemDto orderItemDto = OrderItemDto.builder()
                .bookId(1L)
                .quantity(3)
                .packagingId(1L)
                .couponId(null)
                .build();

        GuestOrderRequestDirect request = GuestOrderRequestDirect.builder()
                .item(orderItemDto)
                .deliveryDate(LocalDate.now())
                .recipientName("테스트")
                .recipientContact("010-1234-5678")
                .readAddress("서울시")
                .detailAddress("강남구")
                .build();

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = factory.create(null, request);

        assertNotNull(order.getGuestId());
        assertEquals(1, order.getItems().size());
        OrderItem item = order.getItems().getFirst();
        assertEquals(1L, item.getBookId());
        assertEquals(BigDecimal.valueOf(10000), item.getPrice());
        assertEquals(BigDecimal.ZERO, item.getDiscountPrice());
        assertEquals(BigDecimal.valueOf(31500), item.getItemTotalPrice()); // 10000 * 3 + 포장지(500 * 3) + 배송비(여기서는 무료)
        assertEquals(BigDecimal.valueOf(500), item.getPackaging().getPackagingPrice());
    }
}
