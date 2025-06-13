package com.ahmad.ProductFinder.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "UpdateInventoryRequest",
        description = "Request DTO for updating existing inventory record details."
)
public class UpdateInventoryRequestDto {

    @Schema(description = "The updated price of the product in this inventory record", example = "35.50", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal price;

    @Schema(description = "The updated stock quantity of the product in this inventory record)", example = "100", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer stockQuantity;

    @Schema(description = "Indicates if the inventory record is active or inactive", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isActive;
}