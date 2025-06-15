package com.ahmad.ProductFinder.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(
        name = "CreateProductRequest",
        description = "Request DTO for creating a new product."
)
public record CreateProductRequestDto(

        @NotBlank(message = "Product name must not be blank")
        @Schema(
                description = "The name of the product",
                example = "Smartphone XYZ",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name,

        @NotBlank(message = "Product description must not be blank")
        @Schema(
                description = "A detailed description of the product",
                example = "A cutting-edge smartphone with a 6.7-inch display and 128GB storage.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String description,

        @NotNull(message = "Product price is required")
        @Schema(
                description = "The price of the product",
                example = "799.99",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        BigDecimal price,

        @NotBlank(message = "Product category must not be blank")
        @Schema(
                description = "The category of the product (e.g., Electronics, Home Goods)",
                example = "Electronics",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String category

) {}