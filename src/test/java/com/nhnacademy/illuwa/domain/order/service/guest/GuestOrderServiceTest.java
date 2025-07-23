package com.nhnacademy.illuwa.domain.order.service.guest;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookCountUpdateRequest;
import com.nhnacademy.illuwa.common.external.product.dto.OrderItemDto;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestCreateRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestCreateResponse;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.GuestOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.factory.GuestOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.service.BookInventoryService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import com.nhnacademy.illuwa.domain.order.service.guest.impl.GuestOrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuestOrderServiceTest {

    @Mock
    GuestOrderDirectFactory guestOrderDirectFactory;
    @Mock
    BookInventoryService bookInventoryService;
    @Mock
    PackagingService packagingService;
    @Mock
    ProductApiClient productApiClient;
    @Mock
    UserApiClient userApiClient;

    @InjectMocks
    GuestOrderServiceImpl guestOrderService;

    @Test
    @DisplayName("guestCreateOrderDirectWithItems: 비회원 주문 생성 성공 & 외부 API 호출 검증")
    void guestCreateOrderDirectWithItems_success() {
        // given
        GuestOrderRequestDirect request = mock(GuestOrderRequestDirect.class);
        OrderItemDto itemDto = mock(OrderItemDto.class);
        when(request.getItem()).thenReturn(itemDto);
        when(itemDto.getBookId()).thenReturn(10L);
        when(itemDto.getQuantity()).thenReturn(2);

        Order createdOrder = mock(Order.class);
        given(guestOrderDirectFactory.create(null, request)).willReturn(createdOrder);

        GuestCreateRequest guestCreateReq = mock(GuestCreateRequest.class);
        try (MockedStatic<GuestCreateRequest> mocked = mockStatic(GuestCreateRequest.class)) {
            mocked.when(() -> GuestCreateRequest.fromGuestOrderRequestDirect(request, createdOrder))
                    .thenReturn(guestCreateReq);

            List<BookCountUpdateRequest> updateList = List.of(mock(BookCountUpdateRequest.class));
            given(bookInventoryService.validateAndCollect(anyList())).willReturn(updateList);
            given(userApiClient.resisterGuest(guestCreateReq))
                    .willReturn(Optional.of(mock(GuestCreateResponse.class)));

            doNothing().when(productApiClient).sendUpdateBooksCount(updateList);

            // when
            Order result = guestOrderService.guestCreateOrderDirectWithItems(request);

            // then
            assertThat(result).isSameAs(createdOrder);

            verify(guestOrderDirectFactory).create(null, request);
            verify(userApiClient).resisterGuest(guestCreateReq);

            ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
            verify(productApiClient).sendUpdateBooksCount(captor.capture());
            assertThat(captor.getValue().size()).isEqualTo(1);

            verify(bookInventoryService).validateAndCollect(anyList());
        }
    }

    @Test
    @DisplayName("getGuestOrderInitDirectData: 도서/포장지 초기 데이터 조회")
    void getGuestOrderInitDirectData_success() {
        // given
        Long bookId = 10L;
        BookItemOrderDto itemDto = mock(BookItemOrderDto.class);
        given(productApiClient.getOrderBookById(bookId)).willReturn(Optional.of(itemDto));

        List<PackagingResponseDto> packagingList = List.of(mock(PackagingResponseDto.class));
        given(packagingService.getPackagingByActive(true)).willReturn(packagingList);

        // when
        GuestOrderInitDirectResponseDto resp = guestOrderService.getGuestOrderInitDirectData(bookId);

        // then
        assertThat(resp.getItem()).isSameAs(itemDto);
        assertThat(resp.getPackaging()).isEqualTo(packagingList);

        verify(productApiClient).getOrderBookById(bookId);
        verify(packagingService).getPackagingByActive(true);
    }
}
