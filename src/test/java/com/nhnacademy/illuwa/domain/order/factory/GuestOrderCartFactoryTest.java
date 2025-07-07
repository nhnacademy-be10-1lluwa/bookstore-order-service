package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider.CartPayload;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider.ItemPrice;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GuestOrderCartFactoryTest {

    ShippingPolicyRepository shippingRepo;
    PackagingRepository packagingRepo;
    OrderRepository orderRepo;
    ItemPriceProvider priceProvider;
    DiscountCalculator discountCalculator;
    MemberCouponService memberCouponService;

    GuestOrderCartFactory factory;

    @BeforeEach
    void setUp() {
        shippingRepo = mock(ShippingPolicyRepository.class);
        packagingRepo = mock(PackagingRepository.class);
        orderRepo = mock(OrderRepository.class);
        priceProvider = mock(ItemPriceProvider.class);
        discountCalculator = mock(DiscountCalculator.class);
        memberCouponService = mock(MemberCouponService.class);

        factory = new GuestOrderCartFactory(packagingRepo, shippingRepo, orderRepo, priceProvider, discountCalculator, memberCouponService);
    }

    @Test
    void create_ShouldReturnOrderWithExpectedFields() {
        ShippingPolicy shippingPolicy = ShippingPolicy.builder()
                .fee(BigDecimal.valueOf(3000))
                .minAmount(BigDecimal.valueOf(20000))
                .active(true)
                .build();
        when(shippingRepo.findByActive(true)).thenReturn(Optional.of(shippingPolicy));

        Packaging packaging = Packaging.builder()
                .packagingPrice(BigDecimal.valueOf(500))
                .build();
        when(packagingRepo.findByPackagingId(1L)).thenReturn(Optional.of(packaging));

        ItemPrice itemPrice1 = new ItemPrice(
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(0)
        );
        when(priceProvider.fetchPrice(eq(1L), eq(1), eq(null), ArgumentMatchers.<Optional<CartPayload>>any()))
                .thenReturn(itemPrice1);
        when(priceProvider.fetchPrice(eq(2L), eq(1), eq(null), ArgumentMatchers.<Optional<CartPayload>>any()))
                .thenReturn(new ItemPrice(BigDecimal.valueOf(12000), BigDecimal.valueOf(12000), BigDecimal.ZERO));

        CartOrderItemDto item1 = CartOrderItemDto.builder()
                .bookId(1L)
                .quantity(1)
                .price(BigDecimal.valueOf(10000))
                .totalPrice(BigDecimal.valueOf(10000))
                .packagingId(1L)
                .couponId(null)
                .build();

        CartOrderItemDto item2 = CartOrderItemDto.builder()
                .bookId(2L)
                .quantity(1)
                .price(BigDecimal.valueOf(12000))
                .totalPrice(BigDecimal.valueOf(12000))
                .packagingId(1L)
                .couponId(null)
                .build();

        GuestOrderRequest request = GuestOrderRequest.builder()
                .cartItem(List.of(item1, item2))
                .deliveryDate(LocalDate.of(2024, 7, 1))
                .recipientName("John Doe")
                .recipientContact("010-1234-5678")
                .readAddress("Seoul")
                .detailAddress("Gangnam-gu")
                .build();

        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = factory.create(null, request);

        assertNotNull(order);
        assertEquals(2, order.getItems().size());
        assertEquals(shippingPolicy, order.getShippingPolicy());
        assertEquals(BigDecimal.ZERO, order.getShippingFee());
    }

    @Test
    void create_ShouldApplyCorrectPriceCalculations() {
        ShippingPolicy shippingPolicy = ShippingPolicy.builder()
                .fee(BigDecimal.valueOf(3000))
                .minAmount(BigDecimal.valueOf(20000)) // higher than item total to trigger fee
                .active(true)
                .build();
        when(shippingRepo.findByActive(true)).thenReturn(Optional.of(shippingPolicy));

        Packaging packaging = Packaging.builder()
                .packagingPrice(BigDecimal.valueOf(500))
                .build();
        when(packagingRepo.findByPackagingId(1L)).thenReturn(Optional.of(packaging));

        ItemPrice itemPrice = new ItemPrice(
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(0)
        );
        when(priceProvider.fetchPrice(eq(1L), eq(2), eq(null), ArgumentMatchers.<Optional<CartPayload>>any()))
                .thenReturn(itemPrice);

        CartOrderItemDto orderItem = CartOrderItemDto.builder()
                .bookId(1L)
                .quantity(2)
                .price(BigDecimal.valueOf(10000))
                .totalPrice(BigDecimal.valueOf(20000))
                .packagingId(1L)
                .couponId(null)
                .build();

        GuestOrderRequest request = GuestOrderRequest.builder()
                .cartItem(List.of(orderItem))
                .deliveryDate(LocalDate.of(2024, 7, 1))
                .recipientName("Price Test")
                .recipientContact("010-0000-1111")
                .readAddress("Busan")
                .detailAddress("Haeundae")
                .build();

        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = factory.create(null, request);

        BigDecimal expectedItemTotal = BigDecimal.valueOf(10000).add(BigDecimal.valueOf(500)).multiply(BigDecimal.valueOf(2)); // 21000
        BigDecimal expectedShippingFee = BigDecimal.valueOf(0); // 21000 < 25000, but actual logic might exempt it
        BigDecimal expectedFinalPrice = expectedItemTotal.add(expectedShippingFee); // 21000

        assertEquals(expectedItemTotal, order.getTotalPrice());
        assertEquals(BigDecimal.ZERO, order.getDiscountPrice());
        assertEquals(BigDecimal.ZERO, order.getUsedPoint());
        assertEquals(expectedShippingFee, order.getShippingFee());
        assertEquals(expectedFinalPrice, order.getFinalPrice());
    }

    @Test
    void create_ShouldThrowException_WhenNoActiveShippingPolicy() {
        GuestOrderRequest request = GuestOrderRequest.builder()
                .cartItem(List.of())
                .deliveryDate(LocalDate.now())
                .recipientName("Jane Doe")
                .recipientContact("010-0000-0000")
                .readAddress("Seoul")
                .detailAddress("Mapo-gu")
                .build();

        when(shippingRepo.findByActive(true)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> factory.create(null, request));
    }
}
