package com.nhnacademy.illuwa.domain.order.controller.user;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookDto;
import com.nhnacademy.illuwa.common.external.user.UserApiClient;
import com.nhnacademy.illuwa.domain.order.dto.order.*;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitDirectResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderInitFromCartResponseDto;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequest;
import com.nhnacademy.illuwa.domain.order.dto.order.member.MemberOrderRequestDirect;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.entity.Order;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import com.nhnacademy.illuwa.domain.order.service.member.MemberOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order/members")
@Tag(name = "회원 API", description = "회원용 주문 API")
public class MemberOrderController {

    private final MemberOrderService memberOrderService;
    private final ProductApiClient productApiClient;

    // 회원 바로 주문하기 내용 조회
    @Operation(summary = "회원 바로 주문 내용 조회", description = "회원이 바로 주문하는데 필요한 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패"),
            @ApiResponse(responseCode = "409", description = "도서 수량 부족")
    })
    @GetMapping(value = "/orders/init/books/{book-id}")
    public ResponseEntity<MemberOrderInitDirectResponseDto> getOrderInitDirect(@RequestHeader("X-USER-ID") Long memberId,
                                                                               @Parameter(name = "bookId", description = "도서 ID", required = true)
                                                                               @PathVariable("book-id") Long bookId) {
        MemberOrderInitDirectResponseDto response = memberOrderService.getOrderInitDirectData(bookId, memberId);
        return ResponseEntity.ok(response);
    }

    // 회원 장바구니 주문하기 내용 조회
    @Operation(summary = "회원 장바구니 주문 내용 조회", description = "회원이 장바구니 주문하는데 필요한 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "주문 도서 정보 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping(value = "/orders/init/cart")
    public ResponseEntity<MemberOrderInitFromCartResponseDto> getOrderInitFromCart(@RequestHeader("X-USER-ID") Long memberId) {
        MemberOrderInitFromCartResponseDto response = memberOrderService.getOrderInitFromCartData(memberId);
        return ResponseEntity.ok(response);
    }

    // 회원 바로 주문하기
    @Operation(summary = "회원 바로 주문하기", description = "회원이 주문 요청한 내용을 즉시 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "주문 도서 정보 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/orders/direct")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequestDirect(@RequestHeader("X-USER-ID") Long memberId, @RequestBody @Valid MemberOrderRequestDirect memberOrderRequestDirect) {
        Order order = memberOrderService.memberCreateOrderDirectWithItems(memberId, memberOrderRequestDirect);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);
        // 책 제목 조회 및 설정
        setTitle(response.getItems());
        return ResponseEntity.ok(response);
    }

    // 회원 장바구니 주문하기
    @Operation(summary = "회원 장바구니 주문하기", description = "회원이 주문 요청한 내용을 즉시 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
            @ApiResponse(responseCode = "404", description = "주문 도서 정보 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/orders/cart")
    public ResponseEntity<OrderCreateResponseDto> sendOrderRequest(@RequestHeader("X-USER-ID") Long memberId, @RequestBody @Valid MemberOrderRequest memberOrderRequest) {
        Order order = memberOrderService.memberCreateOrderFromCartWithItems(memberId, memberOrderRequest);
        OrderCreateResponseDto response = OrderCreateResponseDto.fromEntity(order);

        // 책 제목 조회 및 설정
        setTitle(response.getItems());
        return ResponseEntity.ok(response);
    }

    // 회원 주문 내역들 조회
    @Operation(summary = "회원 주문 내역 조회", description = "회원이 자신의 주문내역들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패"),
    })
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getNewOrdersHistory(@RequestHeader("X-USER-ID") Long memberId,
                                                                   @PageableDefault(size = 10, sort = "orderDate") Pageable pageable) {
        Page<OrderListResponseDto> page = memberOrderService.getOrdersByMemberId(memberId, pageable);
        Map<String, Object> body = Map.of(
                "content", page.getContent(),
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "first", page.isFirst(),
                "last", page.isLast()
        );
        return ResponseEntity.ok(body);
    }


    // 회원 주문 상세 조회
    @Operation(summary = "회원 주문 내역 조회", description = "회원이 자신의 주문내역을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패"),
    })
    @GetMapping(value = "/orders/{order-id}")
    public ResponseEntity<OrderResponseDto> getOrderByOrderId(@RequestHeader("X-USER-ID") Long memberId,
                                                              @Parameter(name = "orderId", description = "주문 ID", required = true)
                                                              @PathVariable("order-id") Long orderId) {
        OrderResponseDto response = memberOrderService.getOrderByMemberIdAndOrderId(memberId, orderId);
        return ResponseEntity.ok(response);
    }

    // 회원의 도서 구매확정 여부 ( 리뷰 도메인 )
    @Operation(summary = "회원 구매확정 여부", description = "회원의 주문내역의 구매 확정 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "조회 실패")
    })
    @GetMapping("/confirmed")
    public ResponseEntity<Boolean> isConfirmedOrder(@RequestHeader("X-USER-ID") Long memberId, @RequestParam Long bookId) {
        return ResponseEntity.ok(memberOrderService.isConfirmedOrder(memberId, bookId));
    }


    /*ㅡㅡㅡㅡㅡ 헬퍼 ㅡㅡㅡㅡㅡㅡ*/

    private void setTitle(List<OrderItemResponseDto> items) {
        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }
    }
}
