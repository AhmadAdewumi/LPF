package com.ahmad.ProductFinder.dtos.response;


import com.ahmad.ProductFinder.models.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponseDto(

        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        LocalDateTime createdAt

) {

    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getCreatedAt()
        );
    }
}

