package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.repository.PackagingRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PackagingRepository packagingRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, PackagingRepository packagingRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.packagingRepository = packagingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getAllOrderItem() {
        return orderItemRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemById(String orderItemId) {
        long id = parseId(orderItemId);

        OrderItem orderItem = orderItemRepository.findByOrderItemId(id).orElseThrow(()
                -> new NotFoundException("해당 주문 상품을 찾을 수 없습니다.", id));

        return toResponseDto(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItemByMemberId(String memberId) {
        long id = parseId(memberId);

        return orderItemRepository.findByMemberId(id).stream().map(this::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItemByOrderId(String orderId) {
        long id = parseId(orderId);

        return orderItemRepository.findByOrderOrderId(id).stream().map(this::toResponseDto).toList();
    }

    // entity -> Dto
    private OrderItemResponseDto toResponseDto(OrderItem item) {
        return new OrderItemResponseDto(item.getOrderItemId(),
                item.getBookId(),
                item.getQuantity(),
                item.getPrice(),
                item.getPackaging().getPackagingId());
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
