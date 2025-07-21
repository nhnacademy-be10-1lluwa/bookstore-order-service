package com.nhnacademy.illuwa.domain.order.service.guest.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookCountUpdateRequest;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.order.dto.extra.BookQuantity;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestCreateRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.GuestOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.service.BookInventoryService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import com.nhnacademy.illuwa.domain.order.service.guest.GuestOrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestOrderServiceImpl implements GuestOrderService {

    private final GuestOrderDirectFactory guestOrderDirectFactory;
    private final BookInventoryService bookInventoryService;

    private final PackagingService packagingService;

    private final ProductApiClient productApiClient;
    private final UserApiClient userApiClient;

    @Override
    public Order guestCreateOrderDirectWithItems(GuestOrderRequestDirect request) {
        Order order = guestOrderDirectFactory.create(null, request);
        GuestCreateRequest guestCreateRequest = GuestCreateRequest.fromGuestOrderRequestDirect(request, order);
        userApiClient.resisterGuest(guestCreateRequest).orElseThrow(()
                -> new BadRequestException("비회원을 등록하지 못하였습니다."));

        List<BookQuantity> quantities = new ArrayList<>();
        quantities.add(new BookQuantity(request.getItem().getBookId(), request.getItem().getQuantity()));

        List<BookCountUpdateRequest> booksToUpdate = bookInventoryService.validateAndCollect(quantities);

        productApiClient.sendUpdateBooksCount(booksToUpdate);

        return order;
    }

    @Override
    public GuestOrderInitDirectResponseDto getGuestOrderInitDirectData(Long bookId) {
        BookItemOrderDto item = productApiClient.getOrderBookById(bookId).orElseThrow(
                () -> new NotFoundException("해당 도서를 찾을 수 없습니다.", bookId));
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        return new GuestOrderInitDirectResponseDto(item, packaging);
    }
}
