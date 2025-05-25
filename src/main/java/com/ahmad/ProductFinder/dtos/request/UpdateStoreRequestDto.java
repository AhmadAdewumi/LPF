package com.ahmad.ProductFinder.dtos.request;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStoreRequestDto {
    @NotBlank(message = "name cannot be blank")
    private String name;

    @Valid
    private AddressDto address;

    private String description;

    private Double latitude;

    private Double longitude;

}
