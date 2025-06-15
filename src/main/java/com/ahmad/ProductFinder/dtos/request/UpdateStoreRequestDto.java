package com.ahmad.ProductFinder.dtos.request;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "UpdateStoreRequest",
        description = "Request DTO for updating an existing store's details. Fields are optional unless specified; only provided fields will be updated."
)
public class UpdateStoreRequestDto {

    @NotBlank(message = "name cannot be blank")
    @Schema(description = "The updated name of the store", example = "Tech Hub Pro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Valid
    @Schema(description = "The updated physical address details of the store (nested json)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AddressDto address;

    @Schema(description = "A new short description or motto for the store", example = "Innovate. Connect. Explore.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "The updated geographical latitude of the store's location", example = "34.0530", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Double latitude;

    @Schema(description = "The updated geographical longitude of the store's location", example = "-118.2500", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Double longitude;
}