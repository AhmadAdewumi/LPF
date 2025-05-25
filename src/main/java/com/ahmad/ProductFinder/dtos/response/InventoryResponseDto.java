package com.ahmad.ProductFinder.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDto {
    private Long productId;
    private String productName;
    private Long storeId;
    private String storeName;
    private String storeAddress;
    private Integer stockQuantity;
    private BigDecimal price;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
