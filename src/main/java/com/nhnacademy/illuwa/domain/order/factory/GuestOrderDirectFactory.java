package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookPriceDto;
import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateGuestId;
import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateOrderNumber;

@Component
@RequiredArgsConstructor
public class GuestOrderDirectFactory {
    private final PackagingRepository packagingRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final OrderRepository orderRepository;
    private final ProductApiClient productApiClient;

    public Order createGuestOrderDirect(GuestOrderRequestDirect request) {
        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByActive(true).orElseThrow(()
                -> new NotFoundException("해당 배송정책을 찾을 수 없습니다."));

        Order emptyOrder = Order.builder().build();

        OrderItem orderItem = buildOrderItem(request, emptyOrder);

        Order order = buildOrderSkeleton(request, shippingPolicy, orderItem);

        orderItem.setOrder(order);

        order.getItems().add(orderItem);

        applyPriceSummary(order, shippingPolicy);

        return orderRepository.save(order);

    }

    private Order buildOrderSkeleton(GuestOrderRequestDirect request, ShippingPolicy shippingPolicy, OrderItem orderItem) {
        Order.OrderBuilder builder = Order.builder()
                .orderNumber(generateOrderNumber(LocalDateTime.now()))
                .guestId(generateGuestId())
                .shippingPolicy(shippingPolicy)
                .deliveryDate(request.getDeliveryDate())
                .totalPrice(orderItem.getItemTotalPrice())
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress());

        applyDefaultOrderFields(builder);

        return builder.build();
    }

    private void applyDefaultOrderFields(Order.OrderBuilder builder) {
        builder
            .discountPrice(BigDecimal.ZERO)
            .usedPoint(BigDecimal.ZERO)
            .orderStatus(OrderStatus.AwaitingPayment);
    }

    private OrderItem buildOrderItem(GuestOrderRequestDirect request, Order order) {

        CartOrderItemDto item = request.getItem();
        Long bookId = item.getBookId();

        BookPriceDto priceDto = productApiClient.getBookPriceByBookId(bookId).orElseThrow(()
                -> new NotFoundException("도서 가격 정보를 찾을 수 없습니다.", bookId));

        BigDecimal unitPrice = priceDto.getPriceSales() != null
                ? priceDto.getPriceSales()
                : priceDto.getPriceStandard();

        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        Packaging packaging = packagingRepository.findByPackagingId(item.getPackagingId())
                .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", item.getPackagingId()));

        return OrderItem.builder()
                .bookId(item.getBookId())
                .order(order)
                .quantity(item.getQuantity())
                .price(unitPrice)
                .discountPrice(BigDecimal.ZERO)
                .itemTotalPrice(totalPrice)
                .packaging(packaging)
                .build();
    }

    private void applyPriceSummary(Order order,
                                   ShippingPolicy shippingPolicy) {
        BigDecimal totalPrice = order.getTotalPrice();

        BigDecimal shippingFee = totalPrice.compareTo(shippingPolicy.getMinAmount()) >= 0
                ? BigDecimal.ZERO
                : shippingPolicy.getFee();

        BigDecimal finalPrice = totalPrice.add(shippingFee);

        order.setFinalPrice(finalPrice);
        order.setShippingFee(shippingFee);
    }
}
