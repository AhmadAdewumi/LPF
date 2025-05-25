package com.ahmad.ProductFinder.dtos.request;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStoreRequestDto(
        @NotBlank String name,

        @Valid AddressDto address,

        String description,


        Double latitude,

        Double longitude,

        @NotNull Long ownerId
) {
}
