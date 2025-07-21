package com.nhnacademy.illuwa.domain.order.service.admin.impl;

import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.BookInventoryService;
import com.nhnacademy.illuwa.domain.order.service.admin.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final BookInventoryService bookInventoryService;


    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getAllOrders(Pageable pageable) {
        return orderRepository.findOrderDtos(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByOrderId(Long orderId) {
        OrderResponseDto orderResponseDto = orderRepository.findOrderDtoByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("해당 주문을 찾을 수 없습니다."));

        List<OrderItemResponseDto> items = orderItemRepository.findOrderItemDtosByOrderId(orderId);
        bookInventoryService.setBookTitles(items); // 제목 설정
        orderResponseDto.setItems(items);
        return orderResponseDto;
    }

    @Override
    public void updateOrderDeliveryDate(Long orderId, LocalDate localDate) {
        orderRepository.updateDeliveryDateByOrderId(orderId, localDate);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByNumber(String orderNumber) {
        return orderRepository.findOrderDtoByOrderNumber(orderNumber).orElseThrow(() ->
                new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrderByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable).map(OrderListResponseDto::orderListResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrderByOrderStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findOrdersDtoByOrderStatus(status, pageable);
    }
}
