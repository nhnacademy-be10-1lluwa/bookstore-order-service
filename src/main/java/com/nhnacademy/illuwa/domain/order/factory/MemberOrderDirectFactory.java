package com.nhnacademy.illuwa.domain.order.factory;


import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookPriceDto;
import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
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
import java.util.Optional;

import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateGuestId;
import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateOrderNumber;

@Component
@RequiredArgsConstructor
public class MemberOrderDirectFactory {
    private final PackagingRepository packagingRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final OrderRepository orderRepository;
    private final ProductApiClient productApiClient;
    private final MemberCouponService memberCouponService;
    private final DiscountCalculator discountCalculator;

    public Order CreateMemberOrderDirect(MemberOrderRequestDirect request) {
        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByActive(true).orElseThrow(()
        -> new NotFoundException("해당 배송정책을 찾을 수 없습니다."));

        Order emptyOrder = Order.builder().build();

        OrderItem orderItem = buildOrderItem(request, emptyOrder);

        Order order = buildOrderSkeleton(request, shippingPolicy);

        orderItem.setOrder(order);

        order.getItems().add(orderItem);

        applyPriceSummary(order);

        return orderRepository.save(order);
    }

    private Order buildOrderSkeleton(MemberOrderRequestDirect request, ShippingPolicy shippingPolicy) {
        return Order.builder()
                .orderNumber(generateOrderNumber(LocalDateTime.now()))
                .guestId(generateGuestId())
                .shippingPolicy(shippingPolicy)
                .deliveryDate(request.getDeliverDate())
                .orderStatus(OrderStatus.AwaitingPayment)
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .build();
    }

    private OrderItem buildOrderItem(MemberOrderRequestDirect request, Order order) {

        CartOrderItemDto item = request.getItem();

        Long bookId = item.getBookId();

        // 도서 가격 정보 조회
        BookPriceDto priceDto = productApiClient.getBookPriceByBookId(bookId).orElseThrow(
                () -> new NotFoundException("도서 가격 정보를 찾을 수 없습니다.", bookId));

        // 포장 옵션 조회
        Packaging packaging = packagingRepository.findByPackagingId(item.getPackagingId())
                .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", item.getPackagingId()));

        // 쿠폰 할인 조회
        Long couponId = request.getItem().getCouponId();
        MemberCouponDiscountDto memberCouponDiscountDto = memberCouponService.getDiscountPrice(couponId);


        BigDecimal unitPrice = priceDto.getPriceSales() != null
                ? priceDto.getPriceSales()
                : priceDto.getPriceStandard();

        BigDecimal grossPrice = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));


        BigDecimal discountedPrice = discountCalculator.calculate( // 할인 적용한 금액 (소비자 계산가)
                grossPrice,
                memberCouponDiscountDto.getDiscountAmount(),
                memberCouponDiscountDto.getDiscountPercent()
        );

        // 할인될 금액
        BigDecimal discountAmount = grossPrice.subtract(discountedPrice);

        // 아이템 별 총 가격 ((도서 단가 * 주문 개수) - 할인 금액 + 포장 옵션 금액)
        BigDecimal itemTotalPrice = discountedPrice.add(packaging.getPackagingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        return OrderItem.builder()
                .bookId(item.getBookId())
                .order(order)
                .quantity(item.getQuantity())
                .price(unitPrice)
                .discountPrice(discountAmount)
                .itemTotalPrice(itemTotalPrice)
                .packaging(packaging)
                .build();

    }

    private void applyPriceSummary(Order order) {

        BigDecimal totalPrice = order.getItems().stream().map(OrderItem::getItemTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        ShippingPolicy shippingPolicy = order.getShippingPolicy();

        Long couponId = order.getMemberCouponId(); // 전체 적용 쿠폰

        BigDecimal usedPoint = Optional.ofNullable(order.getUsedPoint()).orElse(BigDecimal.ZERO); // 사용한 포인트

        BigDecimal shippingFee = totalPrice.compareTo(shippingPolicy.getMinAmount()) >= 0
                ? BigDecimal.ZERO
                : shippingPolicy.getFee();

        BigDecimal discountAmount = BigDecimal.ZERO;

        ///  전체 주문의 할인 / 총액
        if (couponId != null) {

            MemberCouponDiscountDto memberCouponDiscount = memberCouponService.getDiscountPrice(couponId);

            BigDecimal orderAmountAfterCoupon = discountCalculator.calculate(
                    totalPrice,
                    memberCouponDiscount.getDiscountAmount(),
                    memberCouponDiscount.getDiscountPercent()
            );

            // 할인 금액
            discountAmount = totalPrice.subtract(orderAmountAfterCoupon);
        }
        BigDecimal payableAmount = totalPrice.subtract(discountAmount).add(shippingFee);

        order.setTotalPrice(totalPrice);
        order.setDiscountPrice(discountAmount);
        order.setUsedPoint(usedPoint);
        order.setFinalPrice(payableAmount);
        order.setShippingFee(shippingFee);

    }
}
