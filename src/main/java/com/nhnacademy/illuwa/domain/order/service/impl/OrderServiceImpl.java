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
import com.nhnacademy.illuwa.domain.order.repository.ShippingPolicyRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ShippingPolicyRepository shippingPolicyRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ShippingPolicyRepository shippingPolicyRepository) {
        this.orderRepository = orderRepository;
        this.shippingPolicyRepository = shippingPolicyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListResponseDto> getAllOrders() {
        return orderRepository.findOrderItemDtos();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(String orderId) {
        long id = parseId(orderId);

        return orderRepository.findByOrderId(id).map(OrderResponseDto::orderResponseDto).orElseThrow(()
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
    public Order createOrderWithItems(String memberId, OrderCreateRequestDto dto) {

        String orderNumber = generateOrderNumber(dto.getOrderDate());

        long mId = parseId(memberId);
        long orderId = dto.getShippingPolicyId();


        ShippingPolicy shippingPolicy = shippingPolicyRepository.findByShippingPolicyId(orderId).orElseThrow(()
                -> new NotFoundException("해당 배송정책을 찾을 수 없습니다.", orderId));

        // fixme OrderItem 서비스 작성 후 코드 수정 and 쿠폰 작성 후 and 중복 예외 처리
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .memberId(mId)
                .shippingPolicy(shippingPolicy)
                .orderDate(dto.getOrderDate())
                .deliveryDate(dto.getDeliveryDate())
                .totalPrice(dto.getTotalPrice())
                .discountPrice(new BigDecimal("1000"))
                .usedPoint(new BigDecimal("2000"))
                .finalPrice(dto.getTotalPrice())
                .orderStatus(OrderStatus.Pending)
                .build();

        List<OrderItem> items = dto.getItems().stream().map(orderItem
                -> OrderItem.builder()
                .bookId(orderItem.getBookId())
                .order(order)
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .memberCouponId(orderItem.getMemberCouponId())
                .discountPrice(orderItem.getDiscountPrice())
                .itemTotalPrice(orderItem.getPrice().subtract(orderItem.getDiscountPrice()))
                .packaging(orderItem.getPackaging())
                .packagingPrice(orderItem.getPackagingPrice())
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
    private static String generateOrderNumber(ZonedDateTime time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(time);
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
