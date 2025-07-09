package com.nhnacademy.illuwa.domain.order.dto.packaging;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class PackagingCreateRequestDto {

    @NotBlank
    @Size(max = 20)
    private String packagingName;

    @NotNull
    @Positive
    private BigDecimal packagingPrice;
}

// 프론트 -> order 서버 (패키지 추가 요청)
