package com.ahmad.ProductFinder.dtos.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateProductRequestDto(
        String name,
        String description,
        BigDecimal price,
        String category,
        LocalDateTime updatedAt

)
{}
