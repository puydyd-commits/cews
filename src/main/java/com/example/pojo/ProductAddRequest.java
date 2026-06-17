package com.example.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductAddRequest {

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    private String imageUrl;

    private String origin;
}