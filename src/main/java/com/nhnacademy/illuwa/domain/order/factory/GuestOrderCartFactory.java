package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.nhnacademy.illuwa.domain.order.util.generator.OrderNumberGenerator.generateOrderNumber;

@Component
@RequiredArgsConstructor
public class GuestOrderCartFactory {
    private final PackagingRepository packagingRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final OrderRepository orderRepository;

    public Order createGuestOrderCart(GuestOrderRequest request)  {

        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByActive(true).orElseThrow(()
        -> new NotFoundException("해당 배송정책을 찾을 수 없습니다."));

        Order order = buildOrderSkeleton(request, shippingPolicy);

        List<OrderItem> orderItems = buildOrderItems(request, order);

        order.getItems().addAll(orderItems);

        return orderRepository.save(order);
    }



    private Order buildOrderSkeleton(GuestOrderRequest request, ShippingPolicy shippingPolicy) {
        return Order.builder()
                .orderNumber(generateOrderNumber(LocalDateTime.now()))
                .memberId(null)
                .guestId(generateGuestId())
                .shippingFee(shippingPolicy.getFee())
                .shippingPolicy(shippingPolicy)
                .deliveryDate(request.getDeliveryDate())
                .totalPrice(request.getTotalPrice())
                .discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .finalPrice(request.getTotalPrice())
                .orderStatus(OrderStatus.AwaitingPayment)
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .memberCouponId(null)
                .build();
    }

    // 개별 아이템 로직
    private List<OrderItem> buildOrderItems(GuestOrderRequest request, Order order) {
        return request.getCartItem().stream().map(req -> {

            BigDecimal totalPrice = req.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));

            Packaging packaging = packagingRepository.findByPackagingId(req.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", req.getPackagingId()));

            return OrderItem.builder()
                    .bookId(req.getBookId())
                    .order(order)
                    .quantity(req.getQuantity())
                    .price(req.getPrice())
                    .discountPrice(BigDecimal.ZERO)
                    .itemTotalPrice(totalPrice)
                    .packaging(packaging)
                    .build();
        }).toList();
    }

    // 비회원 번호 생성 (12자리)
    private static Long generateGuestId() {
        return Long.parseLong(UUID.randomUUID().toString().replace("\\D", "").substring(0, 12));
    }
}
