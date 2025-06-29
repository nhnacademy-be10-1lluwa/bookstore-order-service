package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
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
    public OrderItemResponseDto getOrderItemById(String orderItemId) {
        long id = parseId(orderItemId);

        return orderItemRepository.findOrderItemDtoByOrderItemId(id).orElseThrow(()
                -> new NotFoundException("해당 주문 상품을 찾을 수 없습니다.", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItemByOrderId(String orderId) {
        long id = parseId(orderId);

        return orderItemRepository.findOrderItemDtosByOrderId(id);
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
