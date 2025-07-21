/*
package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.*;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.PointRequest;
import com.nhnacademy.illuwa.common.external.user.dto.TotalRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.*;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.returnRequest.ReturnRequestCreateRequestDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.entity.types.ReturnReason;
import com.nhnacademy.illuwa.domain.order.exception.common.AccessDeniedException;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto;
import com.nhnacademy.illuwa.domain.order.factory.GuestOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderCartFactory;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.OrderService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final MemberOrderCartFactory memberOrderCartFactory;
    private final ProductApiClient productApiClient;
    private final MemberCouponService memberCouponService;
    private final UserApiClient userApiClient;
    private final PackagingService packagingService;
    private final GuestOrderDirectFactory guestOrderDirectFactory;
    private final MemberOrderDirectFactory memberOrderDirectFactory;


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
        setBookTitles(items); // 제목 설정
        orderResponseDto.setItems(items);
        return orderResponseDto;
    }

   */
/* @Override
    public Order getOrderEntityByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new NotFoundException("해당 주문을 찾을 수 없습니다.")
        );
    }*//*


    @Override
    public void updateOrderDeliveryDate(Long orderId, LocalDate localDate) {
        orderRepository.updateDeliveryDateByOrderId(orderId, localDate);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByMemberIdAndOrderId(Long memberId, Long orderId) {
        OrderResponseDto orderResponseDto = orderRepository.findOrderDtoByMemberIdAndOrderId(memberId, orderId).orElseThrow(()
                -> new AccessDeniedException("해당 주문에 접근할 수 없습니다."));

        List<OrderItemResponseDto> items = orderItemRepository.findOrderItemDtosByOrderId(orderId);
        setBookTitles(items);
        orderResponseDto.setItems(items);
        return orderResponseDto;
    }

    @Override
    public OrderResponseDto getOrderByNumber(String orderNumber) {
        return orderRepository.findOrderDtoByOrderNumber(orderNumber).orElseThrow(() ->
                new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    public Page<OrderListResponseDto> getOrdersByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findOrderListDtoByMemberId(memberId, pageable);
    }

   */
/* @Override
    public OrderResponseDto getOrderByNumberAndContact(String orderNumber, String recipientContact) {
        OrderResponseDto orderResponseDto = orderRepository.findOrderDtoByOrderNumberAndContact(orderNumber, recipientContact)
                .orElseThrow(() -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));

        List<OrderItemResponseDto> items = orderItemRepository.findOrderItemDtosByOrderNumber(orderNumber);
        setBookTitles(items);
        orderResponseDto.setItems(items);

        return orderResponseDto;
    }*//*


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

    // member 주문하기 (cart)
    @Override
    public Order memberCreateOrderFromCartWithItems(Long memberId, MemberOrderRequest request) {

        // 주문 요청(reqeust)에서 각 장바구니 아이템에 적용된 couponId를 추출
        List<Long> couponIds = request.getCartItems().stream()
                .map(CartOrderItemDto::getCouponId)
                .filter(Objects::nonNull)
                .toList();

        handleCoupons(memberId, couponIds);

        Order order = memberOrderCartFactory.create(memberId, request);

        // 포인트 검증
        handleUsedPoint(order.getMemberId(), order.getUsedPoint());

        // 재고 확인 및 처리
        List<BookCountUpdateRequest> booksToUpdate = collectBookCountRequests(
                request.getCartItems().stream()
                        .map(item -> new BookQuantity(item.getBookId(), item.getQuantity()))
                        .toList()
        );
        productApiClient.sendUpdateBooksCount(booksToUpdate);

        return order;
    }

    // member 주문하기 (direct)
    @Override
    public Order memberCreateOrderDirectWithItems(Long memberId, MemberOrderRequestDirect request) {

        handleCoupons(memberId,
                request.getMemberCouponId() == null ? Collections.emptyList() : List.of(request.getMemberCouponId()));

        Order order = memberOrderDirectFactory.create(memberId, request);

        // 포인트 검증
        handleUsedPoint(order.getMemberId(), order.getUsedPoint());

        // 재고 확인 및 처리
        List<BookCountUpdateRequest> booksToUpdate = collectBookCountRequests(
                List.of(new BookQuantity(request.getItem().getBookId(),
                        request.getItem().getQuantity()))
        );
        productApiClient.sendUpdateBooksCount(booksToUpdate);

        return order;
    }


    // guest 주문하기 (direct)
    @Override
    public Order guestCreateOrderDirectWithItems(GuestOrderRequestDirect request) {
        Order order = guestOrderDirectFactory.create(null, request);
        GuestCreateRequest guestCreateRequest = GuestCreateRequest.fromGuestOrderRequestDirect(request, order);
        userApiClient.resisterGuest(guestCreateRequest).orElseThrow(()
                -> new BadRequestException("비회원을 등록하지 못하였습니다."));

        // 재고 확인 및 처리
        List<BookCountUpdateRequest> booksToUpdate = collectBookCountRequests(
                List.of(new BookQuantity(request.getItem().getBookId(),
                        request.getItem().getQuantity()))
        );
        productApiClient.sendUpdateBooksCount(booksToUpdate);


        return order;
    }

    @Override
    public void orderCancel(Long orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
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
        productApiClient.sendRestoreBooksCount(bookCount);

        orderRepository.updateStatusByOrderId(order.getOrderId(), OrderStatus.Cancelled);

        return orderRepository.findOrderDtoByOrderId(order.getOrderId()).orElseThrow(
                () -> new NotFoundException("Order not found: " + order.getOrderId())
        );
    }

    @Override
    public OrderResponseDto refundOrderById(Long orderId, ReturnRequestCreateRequestDto dto) { // 반품 (주문 상에서만 처리)
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));


        long daysSinceDelivery = getDaysSinceDelivery(order.getDeliveryDate(), LocalDate.now());

        BigDecimal returnPrice;

        if (daysSinceDelivery > 30) {
            throw new BadRequestException("바품 기한(30일) 초과");
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
        productApiClient.sendRestoreBooksCount(bookCount);

        return orderRepository.findOrderDtoByOrderId(orderId).orElseThrow(
                () -> new NotFoundException("Order not found: " + orderId)
        );

    }

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

    */
/*@Override
    public void updateOrderStatusByOrderNumber(String orderNumber, OrderUpdateStatusDto orderUpdateDto) {
        orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }*//*


    @Override
    public void updateOrderPaymentByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));

        PointRequest usedPoint = new PointRequest(order.getMemberId(), order.getUsedPoint());
        userApiClient.sendUsedPointByMemberId(usedPoint);

        orderRepository.updateStatusByOrderNumber(orderNumber);
    }

    @Override
    public MemberOrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId) {
        CartResponse cart = productApiClient.getCart(memberId).orElseThrow(()
                -> new NotFoundException("장바구니를 찾을 수 없습니다.", memberId));

        */
/* ㅡㅡㅡㅡㅡㅡ 사용 가능한 쿠폰 로직 ㅡㅡㅡㅡㅡㅡㅡ *//*


        Map<Long, List<MemberCouponDto>> couponMap = new HashMap<>();
        for (BookCartResponse bookCart : cart.getBookCarts()) {
            List<MemberCouponDto> list = new ArrayList<>();

            // 해당 책 전용 쿠폰
            list.addAll(memberCouponService.getAvailableCouponsForBook(
                    memberId, bookCart.getBookId(), CouponType.BOOKS));

            list.addAll(memberCouponService.getAvailableCouponsForCategory(
                    memberId, bookCart.getBookId(), CouponType.CATEGORY
            ));

            list.addAll(memberCouponService.getAvailableCouponsAll(memberId));

            couponMap.put(bookCart.getBookId(), list);
        }



        */
/* ㅡㅡㅡㅡㅡㅡ 사용 가능한 쿠폰 로직 ㅡㅡㅡㅡㅡㅡㅡㅡ*//*


        List<MemberAddressDto> addresses = userApiClient.getAddressByMemberId(memberId);
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId).orElseThrow(()
                -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId));

        return new MemberOrderInitFromCartResponseDto(cart, addresses, couponMap, packaging, pointBalance);
    }

   */
/* @Override
    public GuestOrderInitFromCartResponseDto getGuestOrderInitFromCartData(Long cartId) {
        CreateOrderFromCartRequest request = productApiClient.getGuestCart(cartId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", cartId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);

        return new GuestOrderInitFromCartResponseDto(cartItems, packaging);
    }*//*


    @Override
    public MemberOrderInitDirectResponseDto getOrderInitDirectData(Long bookId, Long memberId) {
        BookItemOrderDto item = productApiClient.getOrderBookById(bookId).orElseThrow(
                () -> new NotFoundException("해당 도서를 찾을 수 없습니다.", bookId));

        if (item.getCount() <= 0) {
            throw new BadRequestException("품절입니다.");
        }

        List<MemberCouponDto> availableCoupons = new ArrayList<>(memberCouponService.getAvailableCouponsForBook(memberId, bookId, CouponType.BOOKS));
        availableCoupons.addAll(memberCouponService.getAvailableCouponsAll(memberId));
        availableCoupons.addAll(memberCouponService.getAvailableCouponsForCategory(memberId, item.getCategoryId(), CouponType.CATEGORY));

        List<MemberAddressDto> addresses = userApiClient.getAddressByMemberId(memberId);
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId).orElseThrow(()
                -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId));

        return new MemberOrderInitDirectResponseDto(item, addresses, availableCoupons, packaging, pointBalance);
    }

    @Override
    public GuestOrderInitDirectResponseDto getGuestOrderInitDirectData(Long bookId) {
        BookItemOrderDto item = productApiClient.getOrderBookById(bookId).orElseThrow(
                () -> new NotFoundException("해당 도서를 찾을 수 없습니다.", bookId));
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        return new GuestOrderInitDirectResponseDto(item, packaging);
    }

    @Override
    public boolean isConfirmedOrder(Long memberId, Long bookId) {
        return orderRepository.existsConfirmedOrderByMemberIdAndBookId(memberId, bookId);
    }

    private record BookQuantity(Long bookId, int quantity) {
    }


    // 책 수량 조회 및 처리
    private List<BookCountUpdateRequest> collectBookCountRequests(List<BookQuantity> items) {
        List<BookCountUpdateRequest> books = new ArrayList<>();
        for (BookQuantity item : items) {
            BookItemOrderDto bookInfo = productApiClient.getOrderBookById(item.bookId())
                    .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다.", item.bookId));
            if (bookInfo.getCount() < item.quantity()) {
                throw new BadRequestException("도서 재고 부족 " + "요청 : "
                        + item.quantity + ", 현재 : " + bookInfo.getCount() + "개");
            }
            books.add(new BookCountUpdateRequest(item.bookId(), item.quantity()));
        }
        return books;
    }

    // 책 제목 설정
    private void setBookTitles(List<OrderItemResponseDto> items) {
        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }
    }

    // 공통 포인트 사용 처리
    private void handleUsedPoint(Long memberId, BigDecimal usedPoint) {
        if (Objects.equals(usedPoint, BigDecimal.ZERO)) {
            return; // 포인트 사용 안함
        }
        BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId));

        if (usedPoint.compareTo(pointBalance) > 0) {
            throw new BadRequestException("소유 포인트를 넘겼습니다.");
        }
    }

    // 쿠폰 검증 및 사용 처리
    private void handleCoupons(Long memberId, List<Long> couponIds) {
        if (couponIds == null || couponIds.isEmpty()) {
            return;
        }

        Set<Long> unique = new HashSet<>(couponIds);
        if (unique.size() != couponIds.size()) {
            throw new BadRequestException("동일한 쿠폰을 여러 상품에 중복 적용할 수 없습니다.");
        }

        for (Long couponId : couponIds) {
            memberCouponService.useCoupon(memberId, couponId);
        }
    }

    // 반품 및 취소 날짜 로직
    private long getDaysSinceDelivery(LocalDate localDate, LocalDate localDate2) {
        return ChronoUnit.DAYS.between(localDate, localDate2);
    }
}
*/
