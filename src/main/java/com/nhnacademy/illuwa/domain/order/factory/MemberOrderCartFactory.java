package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.strategy.ItemPriceProvider;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * [스택킹 정책 안내]
 * 1. 각 아이템에 개별 쿠폰이 적용됨 (buildOrderItems).
 * 2. 이후 전체 주문 단위의 쿠폰이 적용됨 (applyPriceSummary).
 * 3. 사용자가 입력한 포인트는 최종 금액에서 차감.
 * 4. 배송비는 최종 금액(할인 적용 전) 기준으로 산정되며, 최소 주문 금액 미만일 경우 부과됨.
 * <p>
 * 주의: 쿠폰 중복 할인 적용 정책은 기획 정책과 일치해야 하며,
 * 필요시 아이템/전체 쿠폰 중 하나만 허용하도록 검증 필요.
 */

@Component
public class MemberOrderCartFactory extends AbstractOrderFactory<MemberOrderRequest> {
    private final DiscountCalculator discountCalculator;
    private final MemberCouponService memberCouponService;
    public MemberOrderCartFactory(PackagingRepository packagingRepo,
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
    public Order create(Long memberId, MemberOrderRequest request) {
        ShippingPolicy pol = shippingRepo.findByActive(true).orElseThrow(() -> new NotFoundException("배송 정책을 찾을 수 없습니다."));

        // 기본 주문 골격
        Order order = initSkeleton()
                .memberId(memberId)
                .shippingPolicy(pol)
                .shippingFee(pol.getFee())
                .deliveryDate(request.getDeliveryDate())
                .recipientName(request.getRecipientName())
                .recipientContact(request.getRecipientContact())
                .postCode(request.getPostCode())
                .readAddress(request.getReadAddress())
                .detailAddress(request.getDetailAddress())
                .build();

        // 아이템 생성
        List<OrderItem> items = buildOrderItems(request, order);
        order.getItems().addAll(items);

        // 총액/배송비/(주문, 포인트) 계산
        applyPriceSummary(order, pol, request.getUsedPoint(), request.getMemberCouponId(), discountCalculator, memberCouponService);

        return orderRepo.save(order);
    }


    private List<OrderItem> buildOrderItems(MemberOrderRequest request, Order order) {
        return request.getCartItem().stream().map(item -> {
            ItemPriceProvider.CartPayload payload = new ItemPriceProvider.CartPayload(
                    item.getPrice(),
                    item.getQuantity(),
                    item.getTotalPrice()
            );

            ItemPriceProvider.ItemPrice ip = priceProvider.fetchPrice(
                    item.getBookId(),
                    item.getQuantity(),
                    item.getCouponId(),
                    Optional.of(payload)
            );

            Packaging packaging = packagingRepo.findByPackagingId(item.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", item.getPackagingId()));

            BigDecimal packagingFee = packaging.getPackagingPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            BigDecimal itemTotal = ip.netPrice().add(packagingFee);

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

