package com.nhnacademy.illuwa.domain.order.factory;

import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
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
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OrderFactory {
    private final PackagingRepository packagingRepository;
    private final BookPriceApiClient bookPriceApiClient;


    /* ============== private helpers ============== */

    public Order buildOrderSkeleton(OrderCreateRequestDto dto,
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

    public List<OrderItem> buildOrderItems(OrderCreateRequestDto dto, Order order) {

        return dto.getItems().stream().map(req -> {

            // (1) 외부 Book API 호출
            BookPriceDto priceDto = bookPriceApiClient.getBookPriceByBookId(req.getBookId())
                    .orElseThrow(() -> new NotFoundException("도서 가격 정보를 찾을 수 없습니다.", req.getBookId()));

            BigDecimal unitPrice = priceDto.getPriceSales() != null
                    ? priceDto.getPriceSales()
                    : priceDto.getPriceStandard();          // 판매가 null → 정가 사용

            // (2) 할인·총액
            BigDecimal discount = BigDecimal.ZERO;      // TODO 쿠폰 로직
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

    public void applyPriceSummary(Order order,
                                  ShippingPolicy shippingPolicy,
                                  List<OrderItem> items) {

        BigDecimal totalPrice = items.stream()
                .map(OrderItem::getItemTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        /**
         * todo
         * discountPrice -> 할인할 금액
         * usedPoint -> 사용할 포인트
        * */

        BigDecimal discountPrice = BigDecimal.ZERO; // TODO 할인 로직
        BigDecimal usedPoint = BigDecimal.ZERO; // TODO 포인트 사용

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

    private static String generateRandomNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }

        return sb.toString();
    }

}
