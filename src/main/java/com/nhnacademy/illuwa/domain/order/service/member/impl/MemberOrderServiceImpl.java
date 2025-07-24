package com.nhnacademy.illuwa.domain.order.service.member.impl;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookCartResponse;
import com.nhnacademy.illuwa.common.external.product.dto.BookCountUpdateRequest;
import com.nhnacademy.illuwa.common.external.product.dto.CartOrderItemDto;
import com.nhnacademy.illuwa.common.external.product.dto.CartResponse;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.common.external.user.dto.MemberAddressDto;
import com.nhnacademy.illuwa.domain.coupons.dto.memberCoupon.MemberCouponDto;
import com.nhnacademy.illuwa.domain.coupons.entity.status.CouponType;
import com.nhnacademy.illuwa.domain.coupons.service.MemberCouponService;
import com.nhnacademy.illuwa.domain.order.dto.extra.BookQuantity;
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
import com.nhnacademy.illuwa.domain.order.exception.common.AccessDeniedException;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.exception.common.OutOfStockException;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderCartFactory;
import com.nhnacademy.illuwa.domain.order.factory.MemberOrderDirectFactory;
import com.nhnacademy.illuwa.domain.order.repository.OrderItemRepository;
import com.nhnacademy.illuwa.domain.order.repository.OrderRepository;
import com.nhnacademy.illuwa.domain.order.service.BookInventoryService;
import com.nhnacademy.illuwa.domain.order.service.PackagingService;
import com.nhnacademy.illuwa.domain.order.service.member.MemberOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberOrderServiceImpl implements MemberOrderService {

    private final MemberOrderCartFactory memberOrderCartFactory;
    private final MemberOrderDirectFactory memberOrderDirectFactory;

    private final BookInventoryService bookInventoryService;
    private final MemberCouponService memberCouponService;
    private final PackagingService packagingService;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final ProductApiClient productApiClient;
    private final UserApiClient userApiClient;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderListResponseDto> getOrdersByMemberId(Long memberId, Pageable pageable) {
        return orderRepository.findOrderListDtoByMemberId(memberId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByMemberIdAndOrderId(Long memberId, Long orderId) {
        OrderResponseDto orderResponseDto = orderRepository.findOrderDtoByMemberIdAndOrderId(memberId, orderId).orElseThrow(()
                -> new AccessDeniedException("해당 주문에 접근할 수 없습니다."));

        List<OrderItemResponseDto> items = orderItemRepository.findOrderItemDtosByOrderId(orderId);
        bookInventoryService.setBookTitles(items);
        orderResponseDto.setItems(items);
        return orderResponseDto;
    }

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

        // 재고 처리
        List<BookQuantity> quantities = request.getCartItems().stream()
                .map(i -> new BookQuantity(i.getBookId(), i.getQuantity())).toList();

        List<BookCountUpdateRequest> booksToUpdate = bookInventoryService.validateAndCollect(quantities);
        productApiClient.sendUpdateBooksCount(booksToUpdate);

        return order;
    }

    @Override
    public Order memberCreateOrderDirectWithItems(Long memberId, MemberOrderRequestDirect request) {
        handleCoupons(memberId,
                request.getMemberCouponId() == null ? Collections.emptyList() : List.of(request.getMemberCouponId()));

        Order order = memberOrderDirectFactory.create(memberId, request);

        // 포인트 검증
        handleUsedPoint(order.getMemberId(), order.getUsedPoint());

        BookQuantity bookQuantity = new BookQuantity(request.getItem().getBookId(), request.getItem().getQuantity());
        // 재고 처리
        List<BookQuantity> quantities = new ArrayList<>();
        quantities.add(bookQuantity);

        List<BookCountUpdateRequest> booksToUpdate = bookInventoryService.validateAndCollect(quantities);
        productApiClient.sendUpdateBooksCount(booksToUpdate);

        return order;
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public MemberOrderInitDirectResponseDto getOrderInitDirectData(Long bookId, Long memberId) {
        BookItemOrderDto item = productApiClient.getOrderBookById(bookId).orElseThrow(
                () -> new NotFoundException("해당 도서를 찾을 수 없습니다.", bookId));

        if (item.getCount() <= 0) {
            throw new OutOfStockException(item.getBookId());
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
    public boolean isConfirmedOrder(Long memberId, Long bookId) {
        return orderRepository.existsConfirmedOrderByMemberIdAndBookId(memberId, bookId);
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
}
