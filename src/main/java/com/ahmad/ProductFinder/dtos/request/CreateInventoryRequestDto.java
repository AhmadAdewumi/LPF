package com.ahmad.ProductFinder.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "CreateInventoryRequest",
        description = "Request DTO for creating a new inventory record for a product in a specific store."
)
public class CreateInventoryRequestDto {

    @NotNull(message = "store id cannot be null")
    @Positive(message = "Store ID must be a positive number")
    @Schema(description = "The unique ID of the store where the product inventory is being created",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    Long storeId;

    @NotNull(message = "product id cannot be null")
    @Positive(message = "Product ID must be a positive number")
    @Schema(description = "The unique ID of the product for which inventory is being created",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
    Long productId;

    @NotNull(message = "stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Schema(description = "The initial quantity of the product in stock (cannot be negative)",
            example = "50",
            requiredMode = Schema.RequiredMode.REQUIRED)
    Integer stockQuantity;

    @NotNull(message = "price cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    @Schema(description = "The price of the product for this inventory record (must be zero or positive)",
            example = "29.99",
            requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal price;

    @Schema(description = "Indicates if the inventory record is active (default is true)",
            example = "true",
            defaultValue = "true")
    Boolean isActive = true;
}