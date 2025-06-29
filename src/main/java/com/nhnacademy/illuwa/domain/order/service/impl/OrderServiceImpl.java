package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.order.OrderCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderUpdateStatusDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.OrderFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderFactory orderFactory;

    public OrderServiceImpl(OrderRepository orderRepository, OrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.orderFactory = orderFactory;
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

        return orderFactory.createOrder(dto);
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
}
