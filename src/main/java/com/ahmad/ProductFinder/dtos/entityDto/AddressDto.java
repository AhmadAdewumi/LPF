package com.ahmad.ProductFinder.dtos.entityDto;

import com.ahmad.ProductFinder.embedded.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "AddressDto",
        description = "Represents a geographical address with street, city, state, country, and postal code."
)
public record AddressDto(
        @NotBlank
        @Schema(description = "Street name and house number", example = "123 Main Street")
        String street,

        @NotBlank
        @Schema(description = "City name", example = "Ede")
        String city,

        @NotBlank
        @Schema(description = "State or province name", example = "Osun")
        String state,

        @NotBlank
        @Schema(description = "Country name", example = "Nigeria")
        String country,

        @NotBlank
        @Schema(description = "Postal or Zip Code", example = "232101")
        String postalCode
)

{
    public static AddressDto from(Address address) {
        return new AddressDto(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode()
        );
    }
}
