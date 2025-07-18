package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getAllOrderItem() {
        return orderItemRepository.findAllOrderItemDtos();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemById(Long orderItemId) {
        return orderItemRepository.findOrderItemDtoByOrderItemId(orderItemId).orElseThrow(()
                -> new NotFoundException("해당 주문 상품을 찾을 수 없습니다.", orderItemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItemByOrderId(Long orderId) {
        return orderItemRepository.findOrderItemDtosByOrderId(orderId);
    }

}
