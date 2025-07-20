package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Set;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Stores", description = "Operations related to store management and discovery")
public interface StoreDocs {

    @Operation(
            summary = "Create a new store",
            description = "Registers a new store with its name, address, and location.",
            requestBody = @RequestBody(
                    description = "Store details for creation",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateStoreRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Store created successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    ResponseEntity<ApiResponseBody> createStore(CreateStoreRequestDto request);

    @Operation(
            summary = "Update an existing store",
            description = "Updates a store by its ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true)
            },
            requestBody = @RequestBody(
                    description = "Updated store details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateStoreRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error."),
                    @ApiResponse(responseCode = "404", description = "Store not found."),
                    @ApiResponse(responseCode = "500", description = "Internal error.")
            }
    )
    ResponseEntity<ApiResponseBody> updateStore(Long storeId, UpdateStoreRequestDto request);

    @Operation(
            summary = "Disable a store",
            description = "Soft-deactivates a store by its ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Disabled successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Store not found."),
                    @ApiResponse(responseCode = "500", description = "Internal error.")
            }
    )
    ResponseEntity<ApiResponseBody> disableStore(Long storeId);

    @Operation(
            summary = "Delete a store",
            description = "Deletes a store permanently by its ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deleted successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Store not found."),
                    @ApiResponse(responseCode = "500", description = "Internal error.")
            }
    )
    ResponseEntity<ApiResponseBody> deleteStoreUsingStoreId(Long storeId);

    @Operation(
            summary = "Get all stores",
            description = "Returns all registered stores with pagination.",
            parameters = {
                    @Parameter(name = "page", description = "Page number", required = true),
                    @Parameter(name = "size", description = "Page size", required = true),
                    @Parameter(name = "sortBy", description = "Sort field", required = false),
                    @Parameter(name = "sortDirection", description = "ASC or DESC", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stores retrieved successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Error occurred.")
            }
    )
    ResponseEntity<ApiResponseBody> getAllStores(int page, int size, String sortBy, String sortDirection);

    @Operation(
            summary = "Get store by ID",
            description = "Returns a single store by its ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Store retrieved successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Store not found."),
                    @ApiResponse(responseCode = "500", description = "Error occurred.")
            }
    )
    ResponseEntity<ApiResponseBody> getStoreById(Long storeId);

    @Operation(
            summary = "Search nearby stores",
            description = "Finds stores near a given lat/lon within a specified radius (in kilometers).",
            requestBody = @RequestBody(
                    required = true,
                    description = "Latitude, longitude, and radius",
                    content = @Content(schema = @Schema(implementation = NearbyStoreSearchParams.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stores found.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid coordinates."),
                    @ApiResponse(responseCode = "500", description = "Error occurred.")
            }
    )
    ResponseEntity<ApiResponseBody> findNearbyStores(@Parameter(hidden = true) NearbyStoreSearchParams params);

    @Operation(
            summary = "Search stores by name",
            description = "Full-text name search using a keyword.",
            parameters = {
                    @Parameter(name = "storeName", description = "Search keyword", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stores found.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Error occurred.")
            }
    )
    ResponseEntity<ApiResponseBody> searchStoresByStoreName(String storeName);

    @Operation(
            summary = "Nearby stores with product name",
            description = "Finds stores near a given location that have the product name in stock.",
            parameters = {
                    @Parameter(name = "productName", description = "Product name", required = true)
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "Latitude, longitude, and radius",
                    content = @Content(schema = @Schema(implementation = NearbyStoreSearchParams.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stores found.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> findNearbyStoresWithProductName(NearbyStoreSearchParams params, String productName);

    @Operation(
            summary = "Full-text store search",
            description = "Searches stores using PostgreSQL full-text search.",
            parameters = {
                    @Parameter(name = "query", description = "Search keyword", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> fullTextSearch(String query);

    @Operation(
            summary = "Full-text + nearby search",
            description = "Combines full-text and geolocation to return relevant nearby stores.",
            parameters = {
                    @Parameter(name = "query", description = "Search keyword", required = true),
                    @Parameter(name = "lat", description = "Latitude", required = true),
                    @Parameter(name = "lon", description = "Longitude", required = true),
                    @Parameter(name = "radiusKm", description = "Radius in kilometers", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered results.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> searchNearbyStoresUsingFts(String query, double lat, double lon, double radiusKm);

    @Operation(
            summary = "Nearby stores with product ID",
            description = "Returns stores near a location that have a specific product in stock.",
            parameters = {
                    @Parameter(name = "latitude", description = "Latitude", required = true),
                    @Parameter(name = "longitude", description = "Longitude", required = true),
                    @Parameter(name = "radiusInKm", description = "Search radius in kilometers", required = true),
                    @Parameter(name = "productId", description = "Product ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stores with product retrieved.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> findNearbyStoresWithProductId(double latitude, double longitude, double radiusInKm, Long productId);

    @Operation(
            summary = "Assign tags to a store",
            description = "Assigns one or more tags to a specific store.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true)
            },
            requestBody = @RequestBody(
                    description = "List of tag names",
                    required = true,
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string")))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tags assigned successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> assignTagsToStore(Long storeId, Collection<String> tagNames);

    @Operation(
            summary = "Remove a tag from a store",
            description = "Removes a single tag from a specific store.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true),
                    @Parameter(name = "tagName", description = "Tag to remove", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tag removed successfully.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> removeTagFromStore(Long storeId, String tagName);

    @Operation(
            summary = "Find stores by tags",
            description = "Finds stores that match any or all of the specified tags.",
            parameters = {
                    @Parameter(name = "tags", description = "Set of tags to filter by", required = true),
                    @Parameter(name = "matchAll", description = "Whether to match all tags or any", required = true),
                    @Parameter(name = "page", description = "Page number", required = true),
                    @Parameter(name = "size", description = "Page size", required = true),
                    @Parameter(name = "sortBy", description = "Field to sort by", required = false),
                    @Parameter(name = "direction", description = "Sort direction", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered stores using tags found.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
            }
    )
    ResponseEntity<ApiResponseBody> findStoresByTags(Set<String> tags, boolean matchAll, int page, int size, String sortBy, String direction);

//    @Operation(
//            summary = "Find nearby stores by tags",
//            description = "Filters nearby stores by tags, combining spatial and tag-based filtering.",
//            requestBody = @RequestBody(
//                    required = true,
//                    description = "Latitude, longitude, and radius",
//                    content = @Content(schema = @Schema(implementation = NearbyStoreSearchParams.class))
//            ),
//            parameters = {
//                    @Parameter(name = "tags", description = "Set of tags", required = true),
//                    @Parameter(name = "matchAll", description = "Require all tags (true) or any (false)", required = true)
//            },
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Nearby stores with tags returned.", content = @Content(schema = @Schema(implementation = ApiResponseBody.class)))
//            }
//    )
@Operation(
        summary = "Find nearby stores by tags",
        description = "Combine location and tag filters.",
        parameters = {
                @Parameter(name = "tags", description = "Multiple tags. Use repeated param: tags=electronics&tags=groceries", example = "tags=electronics&tags=groceries", array = @ArraySchema(schema = @Schema(type = "string"))),
                @Parameter(name = "matchAll", description = "true = match all tags, false = match any", example = "false"),
                @Parameter(name = "latitude", description = "Latitude", example = "9.0"),
                @Parameter(name = "longitude", description = "Longitude", example = "3.5"),
                @Parameter(name = "radiusInKm", description = "Radius in km", example = "630")
        }
)

ResponseEntity<ApiResponseBody> findNearbyStoresByTags(NearbyStoreSearchParams params, Set<String> tags, boolean matchAll);

}
