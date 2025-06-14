package com.ahmad.ProductFinder.dtos.request;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "CreateStoreRequest",
        description = "Request DTO for creating a new store, including its address and geographical coordinates."
)
public record CreateStoreRequestDto(
        @NotBlank(message = "Store name must not be blank")
        @Schema(description = "The unique name of the store",
                example = "Gadget Galaxy",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Optional: A username associated with the store (only logged in user can create a store), it will automatically be retrieved.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String username,

        @NotNull(message = "Address cannot be null")
        @Valid
        @Schema(description = "The physical address details of the store",
                requiredMode = Schema.RequiredMode.REQUIRED)
        AddressDto address,

        @Schema(description = "A short description or motto of the store",
                example = "Your ultimate destination for cutting-edge technology!")
        String description,

        @Schema(description = "The geographical latitude of the store's location",
                example = "34.0522")
        Double latitude,

        @Schema(description = "The geographical longitude of the store's location",
                example = "-118.2437")
        Double longitude
) {}