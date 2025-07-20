package com.ahmad.ProductFinder.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NearbyStoreSearchParams {
    @Schema(description = "The user's current latitude", example = "6.5244", required = true)
    private double latitude;

    @Schema(description = "The user's current longitude", example = "3.3792", required = true)
    private double longitude;

    @Schema(description = "Search radius in kilometers", example = "5", required = true)
    private double radiusInKm;

    @Schema(description = "Page number (starts from 0)", example = "0")
    private int page = 0;

    @Schema(description = "Number of items per page", example = "10")
    private int size = 10;

    @Schema(description = "Field to sort by", example = "name")
    private String sortBy = "name";

    @Schema(description = "Sort direction: asc or desc", example = "asc")
    private String direction = "asc";
}
