package com.nhnacademy.illuwa.domain.order.dto.orderItem;

import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.OrderItem;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItemResponseDto {

    private Long orderItemId;
    private String title;
    private Long bookId;
    private int quantity;
    private BigDecimal price;
    private Long packagingId;
    private BigDecimal totalPrice;
    private PackagingResponseDto packaging; // <- 추가해야 프론트에서 출력 가능

    @Builder
    public OrderItemResponseDto(Long orderItemId, String title, Long bookId, int quantity, BigDecimal price, Long packagingId, BigDecimal totalPrice, PackagingResponseDto packaging) {
        this.orderItemId = orderItemId;
        this.title = title;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
        this.packagingId = packagingId;
        this.totalPrice = totalPrice;
        this.packaging = packaging;
    }

    @Builder
    @QueryProjection
    public OrderItemResponseDto(Long orderItemId,
                                Long bookId,
                                int quantity,
                                BigDecimal price,
                                Long packagingId,
                                BigDecimal totalPrice,
                                PackagingResponseDto packagingResponseDto) {
        this.orderItemId = orderItemId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
        this.packagingId = packagingId;
        this.totalPrice = totalPrice;
        this.packaging = packagingResponseDto;
    }


    public static OrderItemResponseDto FromOrderItemEntity(OrderItem orderItem) {
        return OrderItemResponseDto.builder()
                .orderItemId(orderItem.getOrderItemId())
                .quantity(orderItem.getQuantity())
                .bookId(orderItem.getBookId())
                .price(orderItem.getPrice())
                .packagingId(orderItem.getPackaging().getPackagingId())
                .totalPrice(orderItem.getItemTotalPrice())
                .packaging(PackagingResponseDto.fromEntity(orderItem.getPackaging()))
                .build();

    }
}

// order 서버 -> 프론트 (개별 주문 상품 조회 요청)