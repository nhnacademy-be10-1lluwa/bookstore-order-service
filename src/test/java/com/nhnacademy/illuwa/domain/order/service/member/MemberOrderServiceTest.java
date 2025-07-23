package com.nhnacademy.illuwa.domain.order.service.member;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.*;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderListResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderCartFactory;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.BookInventoryService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import com.nhnacademy.illuwa.domain.order.service.member.impl.MemberOrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberOrderServiceTest {

    @Mock MemberOrderCartFactory memberOrderCartFactory;
    @Mock MemberOrderDirectFactory memberOrderDirectFactory;

    @Mock BookInventoryService bookInventoryService;
    @Mock MemberCouponService memberCouponService;
    @Mock PackagingService packagingService;

    @Mock OrderRepository orderRepository;
    @Mock OrderItemRepository orderItemRepository;

    @Mock ProductApiClient productApiClient;
    @Mock UserApiClient userApiClient;

    @InjectMocks MemberOrderServiceImpl memberOrderService;

    /* ---------- 조회 계열 ---------- */

    @Test
    @DisplayName("getOrdersByMemberId: Repository 결과 그대로 반환")
    void getOrdersByMemberId_success() {
        Long memberId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);
        Page<OrderListResponseDto> page = new PageImpl<>(List.of(mock(OrderListResponseDto.class)));
        given(orderRepository.findOrderListDtoByMemberId(memberId, pageable)).willReturn(page);

        Page<OrderListResponseDto> result = memberOrderService.getOrdersByMemberId(memberId, pageable);

        assertThat(result).isSameAs(page);
        verify(orderRepository).findOrderListDtoByMemberId(memberId, pageable);
    }

    @Test
    @DisplayName("getOrderByMemberIdAndOrderId: 아이템 세팅 및 제목 설정 호출 검증")
    void getOrderByMemberIdAndOrderId_success() {
        Long memberId = 1L;
        Long orderId = 10L;

        OrderResponseDto dto = new OrderResponseDto();
        given(orderRepository.findOrderDtoByMemberIdAndOrderId(memberId, orderId))
                .willReturn(Optional.of(dto));

        List<OrderItemResponseDto> items = List.of(mock(OrderItemResponseDto.class));
        given(orderItemRepository.findOrderItemDtosByOrderId(orderId)).willReturn(items);

        // when
        OrderResponseDto result = memberOrderService.getOrderByMemberIdAndOrderId(memberId, orderId);

        // then
        assertThat(result.getItems()).isEqualTo(items);
        verify(bookInventoryService).setBookTitles(items);
    }

    /* ---------- 생성(카트 기반) ---------- */

    @Test
    @DisplayName("memberCreateOrderFromCartWithItems: 쿠폰/포인트/재고 처리 정상 동작")
    void memberCreateOrderFromCartWithItems_success() {
        Long memberId = 1L;

        // request & cart items
        MemberOrderRequest request = mock(MemberOrderRequest.class);
        CartOrderItemDto item1 = mock(CartOrderItemDto.class);
        CartOrderItemDto item2 = mock(CartOrderItemDto.class);
        when(item1.getBookId()).thenReturn(100L);
        when(item1.getQuantity()).thenReturn(2);
        when(item1.getCouponId()).thenReturn(11L);

        when(item2.getBookId()).thenReturn(200L);
        when(item2.getQuantity()).thenReturn(1);
        when(item2.getCouponId()).thenReturn(22L);

        when(request.getCartItems()).thenReturn(List.of(item1, item2));

        // order from factory
        Order createdOrder = mock(Order.class);
        when(createdOrder.getMemberId()).thenReturn(memberId);
        when(createdOrder.getUsedPoint()).thenReturn(BigDecimal.valueOf(500)); // 사용 포인트
        given(memberOrderCartFactory.create(memberId, request)).willReturn(createdOrder);

        // 포인트 잔액
        given(userApiClient.getPointByMemberId(memberId)).willReturn(Optional.of(BigDecimal.valueOf(1000)));

        // 재고 업데이트
        List<BookCountUpdateRequest> updateList = List.of(mock(BookCountUpdateRequest.class));
        given(bookInventoryService.validateAndCollect(anyList())).willReturn(updateList);
        doNothing().when(productApiClient).sendUpdateBooksCount(updateList);

        // when
        Order result = memberOrderService.memberCreateOrderFromCartWithItems(memberId, request);

        // then
        assertThat(result).isSameAs(createdOrder);

        // 쿠폰 사용 검증
        verify(memberCouponService).useCoupon(memberId, 11L);
        verify(memberCouponService).useCoupon(memberId, 22L);

        // 재고 업데이트 호출 검증
        ArgumentCaptor<List<BookCountUpdateRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(productApiClient).sendUpdateBooksCount(captor.capture());
        assertThat(captor.getValue()).hasSize(1);

        verify(bookInventoryService).validateAndCollect(anyList());
    }

    @Test
    @DisplayName("memberCreateOrderFromCartWithItems: 동일 쿠폰 중복 -> BadRequestException")
    void memberCreateOrderFromCartWithItems_duplicateCoupon_throws() {
        Long memberId = 1L;
        MemberOrderRequest request = mock(MemberOrderRequest.class);

        CartOrderItemDto item1 = mock(CartOrderItemDto.class);
        CartOrderItemDto item2 = mock(CartOrderItemDto.class);
        when(item1.getCouponId()).thenReturn(11L);
        when(item2.getCouponId()).thenReturn(11L);
        when(request.getCartItems()).thenReturn(List.of(item1, item2));

        assertThatThrownBy(() -> memberOrderService.memberCreateOrderFromCartWithItems(memberId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("동일한 쿠폰");
    }

    @Test
    @DisplayName("memberCreateOrderFromCartWithItems: 포인트 부족 -> BadRequestException")
    void memberCreateOrderFromCartWithItems_pointLack_throws() {
        Long memberId = 1L;
        MemberOrderRequest request = mock(MemberOrderRequest.class);
        when(request.getCartItems()).thenReturn(Collections.emptyList());

        Order createdOrder = mock(Order.class);
        when(createdOrder.getMemberId()).thenReturn(memberId);
        when(createdOrder.getUsedPoint()).thenReturn(BigDecimal.valueOf(5000)); // huge
        given(memberOrderCartFactory.create(memberId, request)).willReturn(createdOrder);

        given(userApiClient.getPointByMemberId(memberId)).willReturn(Optional.of(BigDecimal.valueOf(1000)));

        assertThatThrownBy(() -> memberOrderService.memberCreateOrderFromCartWithItems(memberId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("소유 포인트");
    }

    /* ---------- 생성(바로구매) ---------- */

    @Test
    @DisplayName("memberCreateOrderDirectWithItems: 단건 주문 생성 성공")
    void memberCreateOrderDirectWithItems_success() {
        Long memberId = 1L;
        MemberOrderRequestDirect request = mock(MemberOrderRequestDirect.class);
        when(request.getMemberCouponId()).thenReturn(99L);

        OrderItemDto item = mock(OrderItemDto.class);
        when(item.getBookId()).thenReturn(123L);
        when(item.getQuantity()).thenReturn(3);
        when(request.getItem()).thenReturn(item);

        Order createdOrder = mock(Order.class);
        when(createdOrder.getMemberId()).thenReturn(memberId);
        when(createdOrder.getUsedPoint()).thenReturn(BigDecimal.valueOf(0));
        given(memberOrderDirectFactory.create(memberId, request)).willReturn(createdOrder);

        List<BookCountUpdateRequest> list = List.of(mock(BookCountUpdateRequest.class));
        given(bookInventoryService.validateAndCollect(anyList())).willReturn(list);
        doNothing().when(productApiClient).sendUpdateBooksCount(list);

        Order result = memberOrderService.memberCreateOrderDirectWithItems(memberId, request);

        assertThat(result).isSameAs(createdOrder);
        verify(memberCouponService).useCoupon(memberId, 99L);
        verify(productApiClient).sendUpdateBooksCount(anyList());
    }

    /* ---------- 초기 데이터 ---------- */

    @Test
    @DisplayName("getOrderInitFromCartData: 장바구니/쿠폰/주소/포장지/포인트 조합 반환")
    void getOrderInitFromCartData_success() {
        Long memberId = 10L;

        BookCartResponse bookCart1 = mock(BookCartResponse.class);
        when(bookCart1.getBookId()).thenReturn(501L);
        BookCartResponse bookCart2 = mock(BookCartResponse.class);
        when(bookCart2.getBookId()).thenReturn(502L);

        CartResponse cart = new CartResponse();
        cart.setBookCarts(List.of(bookCart1, bookCart2));
        given(productApiClient.getCart(memberId)).willReturn(Optional.of(cart));

        List<MemberCouponDto> couponsBook = List.of(mock(MemberCouponDto.class));
        List<MemberCouponDto> couponsCat  = List.of(mock(MemberCouponDto.class));
        List<MemberCouponDto> couponsAll  = List.of(mock(MemberCouponDto.class));

        given(memberCouponService.getAvailableCouponsForBook(eq(memberId), anyLong(), eq(CouponType.BOOKS)))
                .willReturn(couponsBook);
        given(memberCouponService.getAvailableCouponsForCategory(eq(memberId), anyLong(), eq(CouponType.CATEGORY)))
                .willReturn(couponsCat);
        given(memberCouponService.getAvailableCouponsAll(memberId)).willReturn(couponsAll);

        List<PackagingResponseDto> packaging = List.of(mock(PackagingResponseDto.class));
        given(packagingService.getPackagingByActive(true)).willReturn(packaging);

        List<com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto> addresses = List.of(mock(com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto.class));
        given(userApiClient.getAddressByMemberId(memberId)).willReturn(addresses);

        given(userApiClient.getPointByMemberId(memberId)).willReturn(Optional.of(BigDecimal.valueOf(777)));

        MemberOrderInitFromCartResponseDto resp = memberOrderService.getOrderInitFromCartData(memberId);

        assertThat(resp.getCartResponse()).isSameAs(cart);
        assertThat(resp.getPackaging()).isSameAs(packaging);
        assertThat(resp.getPointBalance()).isEqualByComparingTo(BigDecimal.valueOf(777));

        // 쿠폰 맵 검증
        assertThat(resp.getCouponMap().keySet()).containsExactlyInAnyOrder(501L, 502L);
        verify(memberCouponService, times(2)).getAvailableCouponsAll(memberId);
    }

    @Test
    @DisplayName("getOrderInitFromCartData: 장바구니 없음 -> NotFoundException")
    void getOrderInitFromCartData_notFoundCart_throws() {
        Long memberId = 1L;
        given(productApiClient.getCart(memberId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberOrderService.getOrderInitFromCartData(memberId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("장바구니");
    }

    @Test
    @DisplayName("getOrderInitDirectData: 품절이면 BadRequestException")
    void getOrderInitDirectData_soldOut_throws() {
        Long memberId = 1L;
        Long bookId = 99L;

        BookItemOrderDto item = mock(BookItemOrderDto.class);
        when(item.getCount()).thenReturn(0);
        given(productApiClient.getOrderBookById(bookId)).willReturn(Optional.of(item));

        assertThatThrownBy(() -> memberOrderService.getOrderInitDirectData(bookId, memberId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("품절");
    }

    @Test
    @DisplayName("getOrderInitDirectData: 정상 반환")
    void getOrderInitDirectData_success() {
        Long memberId = 1L;
        Long bookId = 2L;

        BookItemOrderDto item = mock(BookItemOrderDto.class);
        when(item.getCount()).thenReturn(5);
        when(item.getCategoryId()).thenReturn(7L);

        given(productApiClient.getOrderBookById(bookId)).willReturn(Optional.of(item));

        List<MemberCouponDto> couponsBook = List.of(mock(MemberCouponDto.class));
        List<MemberCouponDto> couponsAll  = List.of(mock(MemberCouponDto.class));
        List<MemberCouponDto> couponsCat  = List.of(mock(MemberCouponDto.class));

        given(memberCouponService.getAvailableCouponsForBook(memberId, bookId, CouponType.BOOKS)).willReturn(couponsBook);
        given(memberCouponService.getAvailableCouponsAll(memberId)).willReturn(couponsAll);
        given(memberCouponService.getAvailableCouponsForCategory(memberId, 7L, CouponType.CATEGORY)).willReturn(couponsCat);

        List<com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto> addresses = List.of(mock(com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto.class));
        given(userApiClient.getAddressByMemberId(memberId)).willReturn(addresses);

        List<PackagingResponseDto> packaging = List.of(mock(PackagingResponseDto.class));
        given(packagingService.getPackagingByActive(true)).willReturn(packaging);

        given(userApiClient.getPointByMemberId(memberId)).willReturn(Optional.of(BigDecimal.valueOf(123)));

        MemberOrderInitDirectResponseDto resp = memberOrderService.getOrderInitDirectData(bookId, memberId);

        assertThat(resp.getItem()).isSameAs(item);
        assertThat(resp.getPackaging()).isSameAs(packaging);
        assertThat(resp.getPointBalance()).isEqualByComparingTo(BigDecimal.valueOf(123));
        assertThat(resp.getAvailableCoupons()).hasSize(3);
    }

    /* ---------- 기타 ---------- */

    @Test
    @DisplayName("isConfirmedOrder: Repository 호출 결과 그대로 반환")
    void isConfirmedOrder_success() {
        given(orderRepository.existsConfirmedOrderByMemberIdAndBookId(1L, 2L)).willReturn(true);
        assertThat(memberOrderService.isConfirmedOrder(1L, 2L)).isTrue();
    }
}
