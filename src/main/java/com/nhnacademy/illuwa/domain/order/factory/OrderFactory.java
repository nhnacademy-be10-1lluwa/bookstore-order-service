package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponseTest;
import com.nhnacademy.illuwa.domain.coupons.entity.MemberCoupon;
import com.nhnacademy.illuwa.domain.coupons.repository.MemberCouponRepository;
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
    private final MemberCouponRepository memberCouponRepository;


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


            MemberCoupon coupon = memberCouponRepository.findMemberCouponById(req.getMemberCouponId()).orElseThrow(()
            -> new NotFoundException("해당 쿠폰 정보를 찾을 수 없습니다.", req.getMemberCouponId()));

            // todo 할인율인지, 힐인 금액인지 확인하는 로직 추가하기

            // todo 할인금액이면 할인 금액을 discount에 추가, 할인율이면 책 가격 * 할인율 = 할인금액을 discount에 추가


            // (2) 할인·총액
            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(req.getQuantity()))
                    .subtract(discount);

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
                    .itemTotalPrice(itemTotal)
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

        /*
         * todo
         * discountPrice -> 할인할 금액
         * usedPoint -> 사용할 포인트
        * */

        Long memberCouponId = order.getMemberCouponId();

        // todo 할인율인지, 힐인 금액인지 확인하는 로직 추가하기

        // todo 할인금액이면 할인 금액을 discount에 추가, 할인율이면 totalPrice * 할인율 = 할인금액을 discount에 추가


        BigDecimal discountPrice = BigDecimal.ZERO; // TODO 할인 로직
        BigDecimal usedPoint = order.getUsedPoint(); // TODO 포인트 사용

        BigDecimal finalPrice = totalPrice.subtract(discountPrice).subtract(usedPoint);

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


    // todo UUID 로 변경하기
    private static String generateRandomNumber() {
        return UUID.randomUUID().toString();
    }

}
