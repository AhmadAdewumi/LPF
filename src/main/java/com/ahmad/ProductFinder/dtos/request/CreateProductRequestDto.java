package com.ahmad.ProductFinder.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequestDto(

        @NotBlank(message = "Product name must not be blank")
        String name,

        @NotBlank(message = "Product description must not be blank")
        String description,

        @NotNull(message = "Product price is required")
        BigDecimal price,

        @NotBlank(message = "Product category must not be blank")
        String category

        ) {}
