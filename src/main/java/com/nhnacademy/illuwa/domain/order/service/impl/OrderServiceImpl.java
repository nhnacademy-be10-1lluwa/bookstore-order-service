package com.nhnacademy.illuwa.domain.order.service.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.*;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.PointRequest;
import com.nhnacademy.illuwa.common.external.user.dto.TotalRequest;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponResponse;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.CouponService;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.guest.*;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemRequestDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.OrderResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.entity.types.OrderStatus;
import com.nhnacademy.illuwa.domain.order.exception.common.AccessDeniedException;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundStringException;
import com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto;
import com.nhnacademy.illuwa.domain.order.factory.GuestOrderCartFactory;
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
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final GuestOrderCartFactory guestOrderCartFactory;
    private final MemberOrderCartFactory memberOrderCartFactory;
    private final ProductApiClient productApiClient;
    private final MemberCouponService memberCouponService;
    private final UserApiClient userApiClient;
    private final PackagingService packagingService;
    private final GuestOrderDirectFactory guestOrderDirectFactory;
    private final MemberOrderDirectFactory memberOrderDirectFactory;
    private final CouponService couponService;


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

        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }

        orderResponseDto.setItems(items);

        return orderResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByMemberIdAndOrderId(Long memberId, Long orderId) {
        OrderResponseDto orderResponseDto = orderRepository.findOrderDtoByMemberIdAndOrderId(memberId, orderId).orElseThrow(()
                -> new AccessDeniedException("해당 주문에 접근할 수 없습니다."));

        List<OrderItemResponseDto> items = orderItemRepository.findOrderItemDtosByOrderId(orderId);

        // 책 제목 조회 및 설정
        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }

        orderResponseDto.setItems(items);

        return orderResponseDto;
    }

    @Override
    public OrderResponseDto getOrderByNumber(String orderNumber) {
        return orderRepository.findOrderDtoByOrderNumber(orderNumber).orElseThrow(() ->
                new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    public Page<OrderListResponseDto> getOrdersByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findOrderListDtoByMemberId(memberId, pageable);
    }

    @Override
    public OrderResponseDto getOrderByNumberAndContact(String orderNumber, String recipientContact) {
        OrderResponseDto orderResponseDto = orderRepository.findOrderDtoByOrderNumberAndContact(orderNumber, recipientContact)
                .orElseThrow(() -> new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));

        List<OrderItemResponseDto> items = orderItemRepository.findOrderItemDtosByOrderNumber(orderNumber);

        // 책 제목 조회 및 설정
        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }

        orderResponseDto.setItems(items);

        return orderResponseDto;
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

    // member 주문하기 (cart)
    @Override
    public Order memberCreateOrderFromCartWithItems(Long memberId, MemberOrderRequest request) {

        // 주문 요청(reqeust)에서 각 장바구니 아이템에 적용된 couponId를 추출
        List<Long> couponIds = request.getCartItems().stream()
                .map(CartOrderItemDto::getCouponId)
                .filter(Objects::nonNull)
                .toList();

        // couponIds를 Set으로 변환 (= 자동중복제거)
        Set<Long> uniqueCouponIds = new HashSet<>(couponIds);

        // set의 크기와 원래 couponIds의 크기가 다르면 중복적용이 있다고 판단
        if (uniqueCouponIds.size() != couponIds.size()) {
            throw new BadRequestException("동일한 쿠폰을 여러 상품에 중복 적용할 수 없습니다.");
        }

        // 회원이 소유한 쿠폰 사용 처리
        for (Long couponId : couponIds) {
            memberCouponService.useCoupon(memberId, couponId);
        }


        Order order = memberOrderCartFactory.create(memberId, request);
        return orderRepository.save(order);
    }

    // member 주문하기 (direct)
    @Override
    public Order memberCreateOrderDirectWithItems(Long memberId, MemberOrderRequestDirect request) {

        // 바로 쿠폰 사용 처리
        if (request.getMemberCouponId() != null) {
            memberCouponService.useCoupon(memberId, request.getMemberCouponId());
        }

        Order order = memberOrderDirectFactory.create(memberId, request);
        if (!Objects.equals(order.getUsedPoint(), BigDecimal.ZERO)) {
            BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId).orElseThrow(()
                    -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId));

            if (order.getUsedPoint().compareTo(pointBalance) >= 1) {
                throw new BadRequestException("소유 포인트를 넘겼습니다.");
            }

            PointRequest pointRequest = new PointRequest(memberId, order.getUsedPoint());
            userApiClient.sendUsedPointByMemberId(pointRequest);
        }
        TotalRequest totalRequest = new TotalRequest(memberId, order.getTotalPrice());
        userApiClient.sendTotalPrice(totalRequest);
        return orderRepository.save(order);
    }

    // guest 주문하기 (cart)
    @Override
    public Order guestCreateOrderFromCartWithItems(Long memberId, GuestOrderRequest request) {
        Order order = guestOrderCartFactory.create(null, request);
        return orderRepository.save(order);
    }

    // guest 주문하기 (direct)
    @Override
    public Order guestCreateOrderDirectWithItems(GuestOrderRequestDirect request) {
        Order order = guestOrderDirectFactory.create(null, request);
        GuestCreateRequest guestCreateRequest = GuestCreateRequest.fromGuestOrderRequestDirect(request, order);
        userApiClient.resisterGuest(guestCreateRequest).orElseThrow(()
                -> new BadRequestException("비회원을 등록하지 못하였습니다."));
        return orderRepository.save(order);
    }


    @Override
    public void cancelOrderById(Long orderId) {
        orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));

        orderRepository.updateOrderStatusByOrderId(orderId, OrderStatus.Cancelled);

    }

    @Override
    public void cancelOrderByOrderNumber(String orderNumber) {
        orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderUpdateStatusDto orderUpdateDto) {
        orderRepository.findByOrderId(orderId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", orderId));

        orderRepository.updateStatusByOrderId(orderId, orderUpdateDto);
    }

    @Override
    public void updateOrderStatusByOrderNumber(String orderNumber, OrderUpdateStatusDto orderUpdateDto) {
        orderRepository.findByOrderNumber(orderNumber).orElseThrow(()
                -> new NotFoundStringException("해당 주문 내역을 찾을 수 없습니다.", orderNumber));
    }

    @Override
    public MemberOrderInitFromCartResponseDto getOrderInitFromCartData(Long memberId) {
        CartResponse cart = productApiClient.getCart(memberId).orElseThrow(()
                -> new NotFoundException("장바구니를 찾을 수 없습니다.", memberId));

        /* ㅡㅡㅡㅡㅡㅡ 사용 가능한 쿠폰 로직 ㅡㅡㅡㅡㅡㅡㅡ */

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



        /* ㅡㅡㅡㅡㅡㅡ 사용 가능한 쿠폰 로직 ㅡㅡㅡㅡㅡㅡㅡㅡ*/

        List<MemberAddressDto> addresses = userApiClient.getAddressByMemberId(memberId);
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);
        BigDecimal pointBalance = userApiClient.getPointByMemberId(memberId).orElseThrow(()
                -> new NotFoundException("보유 포인트를 찾을 수 없습니다.", memberId));

        return new MemberOrderInitFromCartResponseDto(cart, addresses, couponMap, packaging, pointBalance);
    }

    @Override
    public GuestOrderInitFromCartResponseDto getGuestOrderInitFromCartData(Long cartId) {
        CreateOrderFromCartRequest request = productApiClient.getGuestCart(cartId).orElseThrow(()
                -> new NotFoundException("해당 주문 내역을 찾을 수 없습니다.", cartId));

        List<CartOrderItemDto> cartItems = request.getItems();
        List<PackagingResponseDto> packaging = packagingService.getPackagingByActive(true);

        return new GuestOrderInitFromCartResponseDto(cartItems, packaging);
    }

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
}
