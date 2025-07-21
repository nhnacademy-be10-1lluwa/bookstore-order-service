package com.nhnacademy.illuwa.domain.order.service;

import com.nhnacademy.illuwa.common.external.product.ProductApiClient;
import com.nhnacademy.illuwa.common.external.product.dto.BookCountUpdateRequest;
import com.nhnacademy.illuwa.common.external.product.dto.BookDto;
import com.nhnacademy.illuwa.domain.order.dto.extra.BookQuantity;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.orderItem.OrderItemResponseDto;
import com.nhnacademy.illuwa.domain.order.exception.common.BadRequestException;
import com.nhnacademy.illuwa.domain.order.exception.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookInventoryService {

    private final ProductApiClient productApiClient;

    /**
     * 재고를 확인하고 업데이트용 DTO 리스트를 반환한다.
     *
     * @param items 주문·반품 등으로 증감시킬 도서 ID와 수량
     * @return 재고 차감(또는 복구) API 호출용 리스트
     * @throws NotFoundException   도서를 찾을 수 없을 때
     * @throws BadRequestException 재고 부족 시
     */

    public List<BookCountUpdateRequest> validateAndCollect(List<BookQuantity> items) {
        List<BookCountUpdateRequest> result = new ArrayList<>();
        for (BookQuantity item : items) {
            BookItemOrderDto bookInfo = productApiClient.getOrderBookById(item.bookId())
                    .orElseThrow(() -> new NotFoundException("도서를 찾을 수 없습니다.", item.bookId()));
            if (bookInfo.getCount() < item.quantity()) {
                throw new BadRequestException("도서 재고 부족 요청: "
                + item.quantity() + ", 현재: " + bookInfo.getCount() + "개");
            }
            result.add(new BookCountUpdateRequest(item.bookId(), item.quantity()));
        }
        return result;
    }

    // 책 제목 설정
    public void setBookTitles(List<OrderItemResponseDto> items) {
        for (OrderItemResponseDto item : items) {
            BookDto bookDto = productApiClient.getBookById(item.getBookId())
                    .orElseThrow(() -> new NotFoundException("해당 도서를 찾을 수 없습니다.", item.getBookId()));
            item.setTitle(bookDto.getTitle());
        }
    }
}
