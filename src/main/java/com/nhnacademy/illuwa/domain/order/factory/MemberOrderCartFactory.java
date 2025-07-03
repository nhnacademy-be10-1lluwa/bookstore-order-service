package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
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
import java.util.List;
import java.util.Optional;

import static com.nhnacademy.illuwa.domain.order.util.generator.OrderNumberGenerator.generateOrderNumber;

@Component
@RequiredArgsConstructor
public class MemberOrderCartFactory {
    private final PackagingRepository packagingRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final OrderRepository orderRepository;
    private final MemberCouponService memberCouponService;
    private final DiscountCalculator discountCalculator;

    /*
     * [스택킹 정책 안내]
     * 1. 각 아이템에 개별 쿠폰이 적용됨 (buildOrderItems).
     * 2. 이후 전체 주문 단위의 쿠폰이 적용됨 (applyPriceSummary).
     * 3. 사용자가 입력한 포인트는 최종 금액에서 차감.
     * 4. 배송비는 최종 금액 기준으로 산정되며, 최소 주문 금액 미만일 경우 부과됨.
     *
     * 주의: 쿠폰 중복 할인 적용 정책은 기획 정책과 일치해야 하며,
     * 필요시 아이템/전체 쿠폰 중 하나만 허용하도록 검증 필요.
     */

    public Order createMemberOrderCart(Long memberId, MemberOrderRequest request) {

        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByActive(true).orElseThrow(()
                -> new NotFoundException("해당 배송정책을 찾을 수 없습니다."));

        Order order = buildOrderSkeleton(memberId, request, shippingPolicy);

        List<OrderItem> orderItems = buildOrderItems(request, order);

        order.getItems().addAll(orderItems);

        applyPriceSummary(order, shippingPolicy, orderItems);

        return orderRepository.save(order);
    }


    private Order buildOrderSkeleton(Long memberId, MemberOrderRequest request, ShippingPolicy shippingPolicy) {
        return Order.builder()
                .orderNumber(generateOrderNumber(LocalDateTime.now()))
                .memberId(memberId)
                .shippingPolicy(shippingPolicy)
                .orderDate(LocalDateTime.now())
                .deliveryDate(request.getDeliverDate())
                .orderStatus(OrderStatus.AwaitingPayment)
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .build();
    }

    private List<OrderItem> buildOrderItems(MemberOrderRequest request, Order order) {
        return request.getCartItem().stream().map(req -> {

            BigDecimal price = req.getPrice(); // 아이템당 가격

            Long couponId = req.getCouponId();

            MemberCouponDiscountDto memberCouponDiscount = memberCouponService.getDiscountPrice(couponId); // 쿠폰 할인율, 할인 금액

            // 할인 / 총액
            BigDecimal grossPrice = price.multiply(BigDecimal.valueOf(req.getQuantity()));  // == 단가 * 개수

            // 쿠폰 적용할인
            BigDecimal discountedPrice = discountCalculator.calculate(
                    grossPrice,
                    memberCouponDiscount.getDiscountAmount(),
                    memberCouponDiscount.getDiscountPercent()
            );

            // 할인 금액
            BigDecimal discountAmount = grossPrice.subtract(discountedPrice);

            // 포장 옵션
            Packaging packaging = packagingRepository.findByPackagingId(req.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", req.getPackagingId()));

            return OrderItem.builder()
                    .bookId(req.getBookId())
                    .order(order)
                    .quantity(req.getQuantity())
                    .price(price)
                    .memberCouponId(couponId)
                    .discountPrice(discountAmount)
                    .itemTotalPrice(discountedPrice)
                    .packaging(packaging)
                    .build();
        }).toList();
    }

    private void applyPriceSummary(Order order, ShippingPolicy shippingPolicy, List<OrderItem> items) {
        BigDecimal totalPrice = items.stream() // 할인 적용 전 가격
                .map(OrderItem::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long couponId = order.getMemberCouponId(); // 쿠폰

        BigDecimal usedPoint = Optional.ofNullable(order.getUsedPoint()).orElse(BigDecimal.ZERO); // 사용한 포인트

        BigDecimal shippingFee = totalPrice.compareTo(shippingPolicy.getMinAmount()) >= 0 /// 배송비 = (할인 적용 전 가격 >= 배송비 무료 정책 금액) ? 0₩ : 배송비
                ? BigDecimal.ZERO
                : shippingPolicy.getFee();

        /// 전체 주문의 할인 / 총액
        if (couponId != null) {
            MemberCouponDiscountDto memberCouponDiscount = memberCouponService.getDiscountPrice(couponId); // 쿠폰의 내용 조회

            BigDecimal orderAmountAfterCoupon = discountCalculator.calculate( // 쿠폰 할인이 적용된 금액
                    totalPrice,
                    memberCouponDiscount.getDiscountAmount(),
                    memberCouponDiscount.getDiscountPercent()
            );

            // 할인 금액
            BigDecimal discountAmount
                    = totalPrice.subtract(orderAmountAfterCoupon).subtract(usedPoint); // 할인 금액 = 총가격 - 쿠폰 적용할인 - 사용 포인트


            BigDecimal payableAmount
                    = totalPrice.subtract(discountAmount
            ).add(shippingFee); // 할인이 적용된 금액 (지불해야하는 금액) = 전체 금액 - (쿠폰할인 - 포인트 할인) + 배송비

            order.setTotalPrice(totalPrice);
            order.setDiscountPrice(discountAmount);
            order.setUsedPoint(usedPoint);
            order.setFinalPrice(payableAmount);
            order.setShippingFee(shippingFee);
        } else {
            BigDecimal payableAmount = totalPrice.subtract(usedPoint).add(shippingFee);

            order.setTotalPrice(totalPrice);
            order.setDiscountPrice(BigDecimal.ZERO);
            order.setUsedPoint(usedPoint);
            order.setFinalPrice(payableAmount);
            order.setShippingFee(shippingFee);
        }
    }
}
