package com.ahmad.ProductFinder.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "UpdateProductRequest",
        description = "Request DTO for updating an existing product's details. All fields are optional; only provided fields will be updated."
)
public record UpdateProductRequestDto(
        @Schema(description = "The updated name of the product", example = "Smartphone XYZ Pro", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String name,

        @Schema(description = "The updated detailed description of the product", example = "An advanced smartphone with enhanced camera features.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,

        @Schema(description = "The updated price of the product", example = "849.99", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        BigDecimal price,

        @Schema(description = "The updated category of the product", example = "Mobile Phones", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String category
)
{}