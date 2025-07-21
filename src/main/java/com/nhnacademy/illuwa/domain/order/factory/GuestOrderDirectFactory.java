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
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateGuestId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class GuestOrderDirectFactory extends AbstractOrderFactory<GuestOrderRequestDirect> {
    private final PackagingRepository packagingRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final OrderRepository orderRepository;

    @Qualifier("bookApiProvider")
    private final ItemPriceProvider priceProvider;

    private final DiscountCalculator discountCalculator;
    private final MemberCouponService memberCouponService;

    @Autowired
    public GuestOrderDirectFactory(
            PackagingRepository packagingRepository,
            ShippingPolicyRepository shippingPolicyRepository,
            OrderRepository orderRepository,
            @Qualifier("bookApiProvider") ItemPriceProvider priceProvider,
            DiscountCalculator discountCalculator,
            MemberCouponService memberCouponService) {

        super(packagingRepository, shippingPolicyRepository, orderRepository, priceProvider);
        this.priceProvider = priceProvider;
        this.discountCalculator = discountCalculator;
        this.memberCouponService = memberCouponService;
        this.packagingRepository = packagingRepository;
        this.shippingPolicyRepository = shippingPolicyRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(Long memberId, GuestOrderRequestDirect request) {
        ShippingPolicy pol = shippingPolicyRepository.findByActive(true).orElseThrow(()
                -> new NotFoundException("해당 배송정책을 찾을 수 없습니다."));

        Order order = initSkeleton()
                .guestId(generateGuestId())
                .shippingPolicy(pol)
                .shippingFee(pol.getFee())
                .deliveryDate(request.getDeliveryDate())
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .postCode(request.getPostCode())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .build();

        List<OrderItem> items = List.of(buildOrderItem(request, order));
        order.getItems().addAll(items);

        applyPriceSummary(
                order, pol,
                BigDecimal.ZERO,      // usedPoint
                null,                 // orderCoupon
                discountCalculator, memberCouponService);

        return orderRepository.save(order);
    }

    private OrderItem buildOrderItem(GuestOrderRequestDirect request, Order order) {

        OrderItemDto item = request.getItem();
        Long bookId = item.getBookId();

        ItemPrice ip = priceProvider.fetchPrice(
                bookId,
                item.getQuantity(),
                item.getCouponId(),
                Optional.empty());

        Packaging packaging;
        BigDecimal unitPrice = ip.unitPrice();
        BigDecimal totalPrice = BigDecimal.ZERO;

        if (request.getItem().getPackagingId() != null) {
            packaging = packagingRepo.findByPackagingId(item.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", item.getPackagingId()));

            totalPrice = ip.netPrice().add(packaging.getPackagingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        } else {
            packaging = null;
            totalPrice = ip.netPrice();
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
}
