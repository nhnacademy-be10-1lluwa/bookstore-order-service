package com.nhnacademy.illuwa.domain.order.factory;
import java.time.LocalDateTime;

import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateOrderNumber;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
@Transactional
public abstract class AbstractOrderFactory<RQ> {

    protected final PackagingRepository packagingRepo;
    protected final ShippingPolicyRepository shippingRepo;
    protected final OrderRepository orderRepo;
    protected final ItemPriceProvider priceProvider;   // 전략 주입

    public abstract Order create(Long memberId, RQ request);

    protected void initDefaultFields(Order.OrderBuilder builder) {
        builder.discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .orderStatus(OrderStatus.AwaitingPayment);
    }

    /**
     * 공통 주문 빌더 스켈레톤을 생성한다.
     * - 주문번호는 NumberGenerator 로 생성
     * - 기본 할인/포인트/상태 값을 설정
     */
    protected Order.OrderBuilder initSkeleton() {
        Order.OrderBuilder builder = Order.builder()
                .orderDate(LocalDateTime.now())
                .orderNumber(generateOrderNumber(LocalDateTime.now()));
        initDefaultFields(builder);   // 할인·포인트·상태 초기화
        return builder;
    }

    // order Item의 총 합계
    protected BigDecimal calcTotalPrice(List<OrderItem> items) {
        return items.stream().map(OrderItem::getItemTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 쿠폰 할인, 포인트, 배송비 가격 합계
    protected void applyPriceSummary(Order order,
                                     ShippingPolicy shippingPolicy,
                                     BigDecimal usedPoint, Long couponId,
                                     DiscountCalculator discCalc,
                                     MemberCouponService couponSvc) {
        BigDecimal total = calcTotalPrice(order.getItems()); // 아이템의 총 합계

        // 쿠폰 / 포인트 할인
        BigDecimal discount = BigDecimal.ZERO;
        if (couponId != null) {
            MemberCouponDiscountDto dto = couponSvc.getDiscountPrice(couponId);
            BigDecimal after = discCalc.calculate(total, dto.getDiscountAmount(), dto.getDiscountPercent()); // 쿠폰이 적용된 가격
            discount = total.subtract(after); // 할인 금액
        }
        discount = discount.add(usedPoint); // 쿠폰 할인 금액 + 사용한 포인트

        // 배송비
        BigDecimal shipFee = total.compareTo(shippingPolicy.getMinAmount()) >= 0 // 배송비 정책
                ? BigDecimal.ZERO
                : shippingPolicy.getFee();

        order.setTotalPrice(total);
        order.setDiscountPrice(discount);
        order.setUsedPoint(usedPoint);
        order.setShippingFee(shipFee);
        order.setFinalPrice(total.subtract(discount).add(shipFee));
    }
}
