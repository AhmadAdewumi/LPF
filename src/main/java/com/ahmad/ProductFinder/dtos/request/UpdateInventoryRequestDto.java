package com.ahmad.ProductFinder.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequestDto {
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isActive;
}
