package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.entity.ShippingPolicy;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;
    private final PackagingRepository packagingRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ShippingPolicyRepository shippingPolicyRepository, PackagingRepository packagingRepository) {
        this.orderRepository = orderRepository;
        this.shippingPolicyRepository = shippingPolicyRepository;
        this.packagingRepository = packagingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDto> getAllOrders() {
        return orderRepository.findOrderDtos();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(String orderId) {
        long id = parseId(orderId);

        return orderRepository.findOrderDto(id).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDto> getOrderByMemberId(String memberId) {
        long id = parseId(memberId);
        return orderRepository.findByMemberId(id).stream().map(OrderListResponseDto::orderListResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status).stream().map(OrderListResponseDto::orderListResponseDto).toList();
    }

    @Override
    public Order createOrderWithItems(OrderCreateRequestDto dto) {

        // 주문 번호 생성
        String orderNumber = generateOrderNumber(LocalDateTime.now());

        // todo 책 가격 가져오기
        BigDecimal bookPrice = new BigDecimal("20000");
        BigDecimal bookDiscountPrice = new BigDecimal("10000");
        BigDecimal bookTotalPrice = bookPrice.subtract(bookDiscountPrice);

        // 배송 정책 조회
        long shippingPolicyId = dto.getShippingPolicyId();
        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByShippingPolicyId(shippingPolicyId).orElseThrow(()
                -> new NotFoundException("해당 배송정책을 찾을 수 없습니다.", shippingPolicyId));

        // 포장 옵션 조회


        BigDecimal totalPrice = dto.getItems().stream()
                .map(item -> bookPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPrice = BigDecimal.ZERO; // todo 할인 될 가격 로직 추가
        BigDecimal usedPoint = BigDecimal.ZERO; // todo 사용될 포인트 로직 추가
        BigDecimal finalPrice = totalPrice.subtract(discountPrice).subtract(usedPoint);
        BigDecimal shippingFee = null;

        if (shippingPolicy.getMinAmount().compareTo(totalPrice) > 0) { // 최소 금액보다 총 금액이 크면 공짜, 작으면 요금 부여
            shippingFee = shippingPolicy.getFee();
        } else {
            shippingFee = new BigDecimal("0");
        }


        // fixme OrderItem 서비스 작성 후 코드 수정 and 쿠폰 작성 후 and 중복 예외 처리
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .memberId(dto.getMemberId())
                .guestId(dto.getGuestId())
                .shippingPolicy(shippingPolicy)
                .orderDate(LocalDateTime.now())
                .deliveryDate(dto.getRequestedDeliveryDate())
                .totalPrice(totalPrice)
                .discountPrice(discountPrice)
                .usedPoint(usedPoint)
                .finalPrice(finalPrice)
                .orderStatus(OrderStatus.Pending)
                .shippingFee(shippingFee)
                .build();


        // todo 가격 가져오기
        List<OrderItem> items = dto.getItems().stream().map(orderItem
                -> OrderItem.builder()
                .bookId(orderItem.getBookId())
                .order(order)
                .quantity(orderItem.getQuantity())
                .price(bookPrice)
                .memberCouponId(1)
                .discountPrice(bookDiscountPrice)
                .itemTotalPrice(bookTotalPrice)
                .packaging(packagingRepository.findByPackagingId(orderItem.getPackagingId()).orElseThrow(()
                        -> new NotFoundException("해당 포장 옵션을 찾을 수 없습니다.", orderItem.getPackagingId())))
                .build()).toList();

        order.getItems().addAll(items);

        return orderRepository.save(order);
    }

    @Override
    public void cancelOrderById(String orderId) {
        long id = parseId(orderId);
        orderRepository.findByOrderId(id).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", id));

        orderRepository.updateOrderStatusByOrderId(id, OrderStatus.Cancelled);


    }

    @Override
    public void updateOrderStatus(String orderId, OrderUpdateStatusDto orderUpdateDto) {
        long id = parseId(orderId);
        orderRepository.findByOrderId(id).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", id));

        orderRepository.updateOrderStatusByOrderId(id, orderUpdateDto.getOrderStatus());
    }



    // ID 파싱 오류 (잘못된 숫자 포맷)
    private Long parseId(String orderId) {
        try {
            return Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            throw new BadRequestException("유효하지 않은 주문 ID: " + orderId);
        }
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
