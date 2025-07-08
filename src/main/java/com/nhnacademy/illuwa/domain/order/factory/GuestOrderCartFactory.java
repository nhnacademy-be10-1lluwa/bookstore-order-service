package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequest;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider.CartPayload;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider.ItemPrice;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.nhnacademy.illuwa.domain.order.util.generator.NumberGenerator.generateGuestId;

@Component
public class GuestOrderCartFactory extends AbstractOrderFactory<GuestOrderRequest> {


    private final DiscountCalculator discountCalculator;
    private final MemberCouponService memberCouponService;

    @Autowired
    public GuestOrderCartFactory(
            PackagingRepository packagingRepo,
            ShippingPolicyRepository shippingRepo,
            OrderRepository orderRepo,
            @Qualifier("cartPayloadProvider") ItemPriceProvider priceProvider,
            DiscountCalculator discountCalculator,
            MemberCouponService memberCouponService) {

        super(packagingRepo, shippingRepo, orderRepo, priceProvider);
        this.discountCalculator = discountCalculator;
        this.memberCouponService = memberCouponService;
    }


    @Override
    public Order create(Long memberId, GuestOrderRequest request) {

        ShippingPolicy pol = shippingRepo.findByActive(true).orElseThrow(() -> new NotFoundException("배송 정책을 찾을 수 없습니다."));

        // 기본 주문 골격
        Order order = initSkeleton()
                .guestId(generateGuestId())
                .shippingPolicy(pol)
                .shippingFee(pol.getFee())
                .deliveryDate(request.getDeliveryDate())
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .build();

        // 아이템 생성
        List<OrderItem> items = buildOrderItems(request, order);
        order.getItems().addAll(items);

        // 총액/배송비/(주문, 포인트) 계산
        applyPriceSummary(order, pol, BigDecimal.ZERO, null, discountCalculator, memberCouponService);

        return orderRepo.save(order);
    }

    private List<OrderItem> buildOrderItems(GuestOrderRequest request, Order order) {
        return request.getCartItem().stream().map(item -> {
            CartPayload payload = new CartPayload(
                    item.getPrice(),
                    item.getQuantity(),
                    item.getTotalPrice());

            ItemPrice ip = priceProvider.fetchPrice(
                    item.getBookId(),
                    item.getQuantity(),
                    item.getCouponId(),
                    Optional.of(payload)
            );

            Packaging packaging = packagingRepo.findByPackagingId(item.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", item.getPackagingId()));

            BigDecimal packagingFee = packaging.getPackagingPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal itemTotal = item.getTotalPrice().add(packagingFee);

            return OrderItem.builder()
                    .order(order)
                    .bookId(item.getBookId())
                    .quantity(item.getQuantity())
                    .price(ip.unitPrice())
                    .discountPrice(ip.discountAmount())
                    .itemTotalPrice(itemTotal)
                    .packaging(packaging)
                    .build();
        }).toList();
    }
}
