package com.nhnacademy.illuwa.domain.order.service.common.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookCountUpdateRequest;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.PointRequest;
import com.nhnacademy.illuwa.common.external.user.dto.TotalRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.common.CommonOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonOrderServiceImpl implements CommonOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final UserApiClient userApiClient;
    private final ProductApiClient productApiClient;

    @Override
    public void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));
        if (orderUpdateDto.getOrderStatus() == OrderStatus.Confirmed) {
            TotalRequest totalRequest = new TotalRequest(order.getMemberId(), order.getTotalPrice());
            userApiClient.sendTotalPrice(totalRequest);
        }

        orderRepository.updateStatusByOrderId(orderId, orderUpdateDto);
    }

    @Override
    public void updateOrderPaymentByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));

        if (Objects.nonNull(order.getMemberId())) {
            PointRequest usedPoint = new PointRequest(order.getMemberId(), order.getUsedPoint());
            userApiClient.sendUsedPointByMemberId(usedPoint);
        }

        orderRepository.updateStatusByOrderNumber(orderNumber);
    }

    @Override
    public void orderCancel(Long orderId) {
        orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new NotFoundException("해당 주문을 찾을 수 없습니다.", orderId));

        orderItemRepository.deleteByOrderId(orderId);
        orderRepository.deleteByOrderId(orderId);
    }

    @Override
    public OrderResponseDto cancelOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));

        if (!order.getOrderStatus().name().equals("Pending")) {
            throw new BadRequestException("결제 취소 불가능한 상태입니다.");
        }

        // 수량 증가 로직 추가
        List<BookCountUpdateRequest> bookCount = new ArrayList<>();
        order.getItems().forEach(item ->
                bookCount.add(new BookCountUpdateRequest(item.getBookId(), item.getQuantity())));
        productApiClient.sendUpdateBooksCount("positive", bookCount);

        orderRepository.updateStatusByOrderId(order.getOrderId(), OrderStatus.Cancelled);

        return orderRepository.findOrderDtoByOrderId(order.getOrderId()).orElseThrow(
                () -> new NotFoundException("Order not found: " + order.getOrderId())
        );
    }

    @Override
    public OrderResponseDto refundOrderById(Long orderId, ReturnRequestCreateRequestDto dto) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));


        long daysSinceDelivery = getDaysSinceDelivery(order.getDeliveryDate(), LocalDate.now());

        BigDecimal returnPrice;

        if (daysSinceDelivery > 30) {
            throw new BadRequestException("반품 기한(30일) 초과");
        }

        boolean damaged = dto.getReason() == ReturnReason.Defective_Item || dto.getReason() == ReturnReason.Item_Damaged;

        if (damaged) {
            returnPrice = order.getTotalPrice().add(order.getUsedPoint()).add(order.getShippingFee());
        } else if (daysSinceDelivery <= 10) {
            returnPrice =
                    order.getTotalPrice().add(order.getUsedPoint());
        } else {
            throw new BadRequestException("반품 할 수 없습니다.");
        }

        orderRepository.updateOrderStatusByOrderId(orderId, OrderStatus.Returned);

        userApiClient.sendReturnPrice(new TotalRequest(order.getMemberId(), returnPrice));

        // 수량 증가 로직 추가
        List<BookCountUpdateRequest> bookCount = new ArrayList<>();
        order.getItems().forEach(item ->
                bookCount.add(new BookCountUpdateRequest(item.getBookId(), item.getQuantity())));
        productApiClient.sendUpdateBooksCount("positive", bookCount);

        return orderRepository.findOrderDtoByOrderId(orderId).orElseThrow(
                () -> new NotFoundException("Order not found: " + orderId)
        );
    }

    // 반품 및 취소 날짜 로직
    private long getDaysSinceDelivery(LocalDate localDate, LocalDate localDate2) {
        return ChronoUnit.DAYS.between(localDate, localDate2);
    }
}
