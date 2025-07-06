package com.nhnacademy.illuwa.domain.order.factory.strategy;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
public abstract class AbstractOrderFactory<RQ> {
    protected final PackagingRepository packagingRepo;
    protected final ShippingPolicyRepository shippingRepo;
    protected final OrderRepository orderRepo;
    protected final ItemPriceProvider priceProvider;   // 전략 주입

    public abstract Order create(RQ request);

    protected void initDefaultFields(Order.OrderBuilder builder) {
        builder.discountPrice(BigDecimal.ZERO)
                .usedPoint(BigDecimal.ZERO)
                .orderStatus(OrderStatus.AwaitingPayment);
    }

    protected BigDecimal calcTotalPrice(List<OrderItem> items) {
        return items.stream().map(OrderItem::getItemTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected void applyPriceSummary(Order order,
                                     ShippingPolicy shippingPolicy,
                                     BigDecimal usedPoint, Long couponId,
                                     DiscountCalculator discCalc,
                                     MemberCouponService couponSvc) {
        BigDecimal total = calcTotalPrice(order.getItems());

        // 쿠폰 / 포인트 할인
        BigDecimal discount = BigDecimal.ZERO;
        if (couponId != null) {
            MemberCouponDiscountDto dto = couponSvc.getDiscountPrice(couponId);
            BigDecimal after = discCalc.calculate(total, dto.getDiscountAmount(), dto.getDiscountPercent());
            discount = total.subtract(after);
        }
        discount = discount.add(usedPoint);

        // 배송비
        BigDecimal shipFee = total.compareTo(shippingPolicy.getMinAmount()) >= 0
                ? BigDecimal.ZERO
                : shippingPolicy.getFee();

        order.setTotalPrice(total);
        order.setDiscountPrice(discount);
        order.setUsedPoint(usedPoint);
        order.setShippingFee(shipFee);
        order.setFinalPrice(total.subtract(discount).add(shipFee));
    }
}
