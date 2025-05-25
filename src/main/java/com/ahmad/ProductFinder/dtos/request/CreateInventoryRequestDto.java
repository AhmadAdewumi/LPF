package com.ahmad.ProductFinder.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInventoryRequestDto {
    @NotNull(message = "store id cannot be null")
    @Positive
    Long storeId;
    @NotNull(message = "product id cannot be null")
    @Positive
    Long productId;
    @NotNull(message = "stock quantity cannot be null")
    @Min(0)
    Integer stockQuantity;
    @NotNull(message = "price cannot be null")
    @DecimalMin("0.0")
    BigDecimal price;
    Boolean isActive = true;
}
