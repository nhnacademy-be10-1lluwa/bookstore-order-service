package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDiscountDto;
import com.nhnacademy.illuwa.domain.coupons.factory.DiscountCalculator;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.Packaging;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.external.book.BookPriceApiClient;
import com.nhnacademy.illuwa.domain.order.external.book.BookPriceDto;
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

@Component
@RequiredArgsConstructor
public class OrderFactory {
    private final PackagingRepository packagingRepository;
    private final BookPriceApiClient bookPriceApiClient;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final OrderRepository orderRepository;
    private final MemberCouponService memberCouponService;
    private final DiscountCalculator discountCalculator;


    public Order createOrder(OrderCreateRequestDto dto) {
        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByShippingPolicyId(dto.getShippingPolicyId())
                .orElseThrow(() -> new NotFoundException("해당 배송정책을 찾을 수 없습니다.", dto.getShippingPolicyId()));

        Order order = buildOrderSkeleton(dto, shippingPolicy);

        List<OrderItem> orderItems = buildOrderItems(dto, order);

        order.getItems().addAll(orderItems);

        applyPriceSummary(order, shippingPolicy, orderItems);

        return orderRepository.save(order);
    }


    private Order buildOrderSkeleton(OrderCreateRequestDto dto,
                                     ShippingPolicy shippingPolicy) {

        return Order.builder()
                .orderNumber(generateOrderNumber(LocalDateTime.now()))
                .memberId(dto.getMemberId())
                .guestId(dto.getGuestId())
                .shippingPolicy(shippingPolicy)
                .orderDate(LocalDateTime.now())
                .deliveryDate(dto.getRequestedDeliveryDate())
                .orderStatus(OrderStatus.Pending)
                .build();
    }

    private List<OrderItem> buildOrderItems(OrderCreateRequestDto dto, Order order) {

        return dto.getItems().stream().map(req -> {

            // (1) 외부 Book API 호출
            BookPriceDto priceDto = bookPriceApiClient.getBookPriceByBookId(req.getBookId())
                    .orElseThrow(() -> new NotFoundException("도서 가격 정보를 찾을 수 없습니다.", req.getBookId()));

            BigDecimal unitPrice = priceDto.getPriceSales() != null
                    ? priceDto.getPriceSales()
                    : priceDto.getPriceStandard();          // 판매가 null → 정가 사용


            MemberCouponDiscountDto coupon = memberCouponService.getDiscountPrice(order.getMemberCouponId());

            // (2) 할인·총액
            BigDecimal grossPrice = unitPrice.multiply(BigDecimal.valueOf(req.getQuantity()));

            // 쿠폰 적용할인
            BigDecimal discountedPrice = discountCalculator.calculate(
                    grossPrice,
                    coupon.getDiscountAmount(),
                    coupon.getDiscountPercent());

            // 할인 금액
            BigDecimal discount = grossPrice.subtract(discountedPrice);

            // 최종 결제 금액

            // (3) 포장 옵션
            Packaging packaging = packagingRepository.findByPackagingId(req.getPackagingId())
                    .orElseThrow(() -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", req.getPackagingId()));

            return OrderItem.builder()
                    .bookId(req.getBookId())
                    .order(order)
                    .quantity(req.getQuantity())
                    .price(unitPrice)
                    .memberCouponId(req.getMemberCouponId())
                    .discountPrice(discount)
                    .itemTotalPrice(discountedPrice)
                    .packaging(packaging)
                    .build();
        }).toList();
    }

    private void applyPriceSummary(Order order,
                                   ShippingPolicy shippingPolicy,
                                   List<OrderItem> items) {

        BigDecimal totalPrice = items.stream()
                .map(OrderItem::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        MemberCouponDiscountDto coupon = memberCouponService.getDiscountPrice(order.getMemberCouponId());


        // (2) 할인·총액

        // 쿠폰 적용할인 (할인이 적용된 금액)
        BigDecimal discountedPrice = discountCalculator.calculate(
                totalPrice,
                coupon.getDiscountAmount(),
                coupon.getDiscountPercent());

        // 할인 금액
        BigDecimal discountPrice = totalPrice.subtract(discountedPrice);

        // 사용 포인트
        BigDecimal usedPoint = order.getUsedPoint();

        BigDecimal finalPrice = discountedPrice.subtract(usedPoint);

        BigDecimal shippingFee = totalPrice.compareTo(shippingPolicy.getMinAmount()) >= 0
                ? BigDecimal.ZERO
                : shippingPolicy.getFee();

        order.setTotalPrice(totalPrice);
        order.setDiscountPrice(discountPrice);
        order.setUsedPoint(usedPoint);
        order.setFinalPrice(finalPrice);
        order.setShippingFee(shippingFee);
    }

    // 주문 번호 생성 메서드
    private static String generateOrderNumber(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentTime = time.format(formatter);
        String randomNumber = generateRandomNumber();

        return currentTime + "-" + randomNumber;
    }


    // 총 10자리 uuid 만들기
    private static String generateRandomNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

}
