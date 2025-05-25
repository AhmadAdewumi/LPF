package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.models.Inventory;

import java.math.BigDecimal;

public record ProductInventoryDto(
        Long productId,
        String productName,
        BigDecimal price,
        int stockQuantity
) {
    public static ProductInventoryDto from(Inventory inv) {
        return new ProductInventoryDto(
                inv.getProduct().getId(),
                inv.getProduct().getName(),
                inv.getPrice(),
                inv.getStockQuantity()
        );
    }
}