package com.ahmad.ProductFinder.dtos.entityDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "Store",
        description = "Represents basic information about a store."
)
public class StoreDto {

    @Schema(description = "The name of the store", example = "Adewumi's store")
    private String storeName;

    @Schema(description = "The physical address of the store", example = "10 Olaiya Okegada")
    private String storeAddress;

    @Schema(description = "A brief description of the store", example = "Your one-stop shop for all sort of electronics!")
    private String description;
}