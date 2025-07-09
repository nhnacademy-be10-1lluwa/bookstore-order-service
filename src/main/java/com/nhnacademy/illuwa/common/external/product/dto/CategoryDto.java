package com.nhnacademy.illuwa.common.external.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CategoryDto {
    @JsonProperty("id")
    private Long categoryId;
    private Long parentCategoryId;
    private String categoryName;
}
