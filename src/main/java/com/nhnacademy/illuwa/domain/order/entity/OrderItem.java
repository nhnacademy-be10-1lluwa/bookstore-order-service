package com.nhnacademy.illuwa.domain.order.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {

    @Id
    private long orderItemId;

    // 도서 API 사용하여 ID값 가져오기
    private long bookId;

    @ManyToOne
    private Order order;

    @Setter
    private int quantity;

    @Setter
    private BigDecimal price; // 항목별 구매가격

    @ManyToOne
    private Packaging packaging;

    // fixme 관계 테이블 확인 후 생성자 코드 작성
}
