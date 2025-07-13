package com.nhnacademy.illuwa.domain.order.dto.order.guest;

import com.nhnacademy.illuwa.domain.order.dto.orderItem.BookItemOrderDto;
import com.nhnacademy.illuwa.domain.order.dto.packaging.PackagingResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestOrderInitDirectResponseDto {
    private BookItemOrderDto item;
    private List<PackagingResponseDto> packaging;
}
