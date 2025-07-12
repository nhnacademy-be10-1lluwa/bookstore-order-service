package com.nhnacademy.illuwa.domain.order.factory;



import com.nhnacademy.illuwa.common.external.product.dto.OrderItemDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Component
public class MemberOrderDirectFactory extends AbstractOrderFactory<MemberOrderRequestDirect> {

    private final DiscountCalculator discountCalculator;
    private final MemberCouponService memberCouponService;

    @Autowired
    public MemberOrderDirectFactory(PackagingRepository packagingRepo,
                                    ShippingPolicyRepository shippingRepo,
                                    OrderRepository orderRepo,
                                    @Qualifier("bookApiProvider") ItemPriceProvider priceProvider,
                                    DiscountCalculator discountCalculator,
                                    MemberCouponService memberCouponService) {
        super(packagingRepo, shippingRepo, orderRepo, priceProvider);
        this.discountCalculator = discountCalculator;
        this.memberCouponService = memberCouponService;
    }

    @Override
    public Order create(Long memberId, MemberOrderRequestDirect request) {
        ShippingPolicy pol = shippingRepo.findByActive(true).orElseThrow(()
                -> new NotFoundException("해당 배송정책을 찾을 수 없습니다."));

        Order order = initSkeleton()
                .memberId(memberId)
                .shippingPolicy(pol)
                .deliveryDate(request.getDeliveryDate())
                .postCode(request.getPostCode())
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .build();

        List<OrderItem> items = List.of(buildOrderItem(request, order));
        order.getItems().addAll(items);


        applyPriceSummary(
                order, pol,
                request.getUsedPoint(),      // usedPoint
                request.getMemberCouponId(),                 // orderCoupon
                discountCalculator, memberCouponService);

        return orderRepo.save(order);
    }

    public OrderItem buildOrderItem(MemberOrderRequestDirect request, Order order) {

        OrderItemDto item = request.getItem();
        Long bookId = item.getBookId();

        ItemPriceProvider.ItemPrice ip = priceProvider.fetchPrice(
                bookId,
                item.getQuantity(),
                item.getCouponId(),
                Optional.empty());

        BigDecimal unitPrice = ip.unitPrice();
        BigDecimal totalPrice = ip.netPrice();

        Packaging packaging;
        if (request.getItem().getPackagingId() != null) {
            packaging = packagingRepo.findByPackagingId(item.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", item.getPackagingId()));
        } else {
            packaging = null;
        }

        return OrderItem.builder()
                .bookId(item.getBookId())
                .order(order)
                .quantity(item.getQuantity())
                .price(unitPrice)
                .discountPrice(ip.discountAmount())
                .itemTotalPrice(totalPrice)
                .packaging(packaging)
                .build();


    }

    public void applyPriceSummary(Order order) {

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
