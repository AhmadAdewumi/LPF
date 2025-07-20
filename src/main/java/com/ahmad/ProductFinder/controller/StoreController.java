package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.*;
import com.ahmad.ProductFinder.service.store.nearbyStoreService.INearbyStoreService;
import com.ahmad.ProductFinder.service.store.nearbyStoreService.NearbyStoreService;
import com.ahmad.ProductFinder.service.store.storeService.IStoreService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping("/api/v1/store")
@Tag(name = "Store Management", description = "APIs for creating, updating, retrieving, deleting, and searching stores, including location-based searches and product availability.")
public class StoreController {
    private final IStoreService storeService;
    private final INearbyStoreService nearbyStoreService;

    public StoreController(IStoreService storeService, INearbyStoreService nearbyStoreService) {
        this.storeService = storeService;
        this.nearbyStoreService = nearbyStoreService;
    }

    @Operation(
            summary = "Create a new store",
            description = "Registers a new store in the system with its name, address(nested json), and geographical location(using the stores longitude and latitude).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Store details for creation of new store",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateStoreRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Store created successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid store data provided (e.g., missing name, invalid coordinates(i.e latitude and longitude))."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during store creation."
                    )
            }
    )
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> createStore(@RequestBody CreateStoreRequestDto request) {
        log.info("Creating store with name: {}", request.name());
        StoreResponseDto result = storeService.createStore(request);
        log.info("Store created successfully with ID: {}", result.id());
        return ResponseEntity.ok(new ApiResponseBody("Store created successfully", result));
    }


    @Operation(
            summary = "Update an existing store",
            description = "Updates the details of an existing store identified by its ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "The unique ID of the store to update", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated store details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateStoreRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store updated successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid update data provided or validation errors."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Store with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during store update."
                    )
            }
    )
    @PatchMapping(value = "/update/{storeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateStore(@PathVariable Long storeId,
                                                       @RequestBody @Valid UpdateStoreRequestDto request) {
        log.info("Updating store with ID: {}", storeId);
        StoreResponseDto updatedStore = storeService.updateStore(storeId, request);
        log.info("Store with ID: {} updated successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store With ID " + storeId + " Updated Successfully !", updatedStore));
    }

    @Operation(
            summary = "Disable a store by ID",
            description = "Disables a store from the system based on its unique ID. This action is irreversible and will " +
                    "also remove associated inventory records.",
            parameters = {
                    @Parameter(name = "storeId", description = "The unique ID of the store to disable", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store disabled successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Store with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred while disabling store."
                    )
            }
    )
//    @Hidden
    @PatchMapping("/disable/{storeId}")
    public ResponseEntity<ApiResponseBody> disableStore(@PathVariable Long storeId) {
        log.info("Disabling store with ID: {}", storeId);
        storeService.disableStore(storeId);
        log.info("Store with ID: {} disabled successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " deactivated successfully !", null));
    }

    @Hidden
    @PatchMapping(value = "/restore/{storeId}")
    public ResponseEntity<ApiResponseBody> restoreUser(@PathVariable Long storeId) {
        log.info("Restore Store endpoint called");
        StoreResponseDto result = storeService.restoreStore(storeId);
        log.info("Store restored successfully");
        return ResponseEntity.ok(new ApiResponseBody("Store restored Successfully", result));
    }


    @Operation(
            summary = "Deletes a store by ID",
            description = "Deletes a store from the system based on its unique ID. This action is irreversible and will also remove associated inventory records.",
            parameters = {
                    @Parameter(name = "storeId", description = "The unique ID of the store to delete", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store deleted successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Store with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during store deletion."
                    )
            }
    )
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ApiResponseBody> deleteStoreUsingStoreId(@PathVariable Long storeId) {
        log.info("Deleting store with ID: {}", storeId);
        storeService.deleteStore(storeId);
        log.info("Store with ID: {} deleted successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " deleted successfully !", null));
    }

    @Operation(
            summary = "Get all stores",
            description = "Retrieves a list of all stores currently registered in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All stores retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllStores(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "name") String sortBy,
                                                        @RequestParam(defaultValue = "asc") String sortDirection) {
        log.info("Fetching all stores - Page: {}, Size: {}, SortBy: {}, Direction: {}", page, size, sortBy, sortDirection);

        PagedResponseDto<StoreResponseDto> stores = storeService.getAllStores(page, size, sortBy, sortDirection);

        log.info("Fetched {} store(s) for page {}", stores.getContent().size(), page);
        return ResponseEntity
                .ok(new ApiResponseBody("All stores retrieved successfully!", stores));
    }


    @Operation(
            summary = "Get store by ID",
            description = "Retrieves the details of a single store by its unique ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "The unique ID of the store to retrieve", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Store retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Store with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponseBody> getStoreById(@PathVariable Long storeId) {
        log.info("Fetching store with ID: {}", storeId);
        StoreResponseDto store = storeService.getStoreUsingStoreId(storeId);
        log.info("Store with ID: {} fetched successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " retrieved successfully !", store));
    }

    @Operation(
            summary = "Find nearby stores by geographical coordinates",
            description = "Retrieves a list of stores located within a specified radius (in kilometers) of given latitude and longitude coordinates.",
//            parameters = {
//                    @Parameter(name = "latitude", description = "the user's current latitude", required = true, example = "34.0522"),
//                    @Parameter(name = "longitude", description = "thr user's current longitude", required = true, example = "-118.2437"),
//                    @Parameter(name = "radiusInKm", description = "Search radius in kilometers", required = true, example = "5.0")
//            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nearby stores fetched successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid coordinates or radius."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during search."
                    )
            }
    )
    @GetMapping(value = "/nearby")
    public ResponseEntity<ApiResponseBody> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params) {
        log.info("Searching for nearby stores at lat: {}, long: {} within radius: {} km", params.getLatitude(), params.getLongitude(), params.getRadiusInKm());
        PagedResponseDto<NearbyStoreResponseDto> results = nearbyStoreService.findNearbyStores(params);
        log.info("Found {} nearby store(s)", results.getTotalElements());
        return ResponseEntity.ok(new ApiResponseBody("Nearby Stores Fetched Successfully ! ", results));
    }

    @Operation(
            summary = "Search stores by name keyword",
            description = "Retrieves a list of stores whose names contain the specified keyword, including their associated inventory details.",
            parameters = {
                    @Parameter(name = "storeName", description = "Keyword to search for in store names", required = true, example = "Electronics Hub")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stores matching the provided name retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during search."
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchStoresByStoreName(@RequestParam String storeName) {
        log.info("Searching stores with name like: {}", storeName);
        List<StoreWithInventoryDto> results = storeService.searchStoresUsingStoreName(storeName);
        log.info("Found {} store(s) matching '{}'", results.size(), storeName);
        return ResponseEntity
                .ok(new ApiResponseBody(format("Stores matching value,'%s' , provided successfully !", storeName), results));
    }

    @Operation(
            summary = "Find nearby stores with a specific product name in stock",
            description = "Retrieves a list of stores located within a specified radius that also have a product matching the given name in stock.",
            parameters = {
                    @Parameter(name = "latitude", description = "Current latitude of the user", required = true, example = "34.0522"),
                    @Parameter(name = "longitude", description = "Current longitude of the search center", required = true, example = "-118.2437"),
                    @Parameter(name = "radiusInKm", description = "Search radius in kilometers", required = true, example = "10.0"),
                    @Parameter(name = "productName", description = "Name of the product to filter by in nearby stores", required = true, example = "Laptop")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nearby stores with the specified product fetched successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid coordinates, radius, or product name."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - No nearby stores found with the product."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during search."
                    )
            }
    )
    @GetMapping("/product/search")
    public ResponseEntity<ApiResponseBody> findNearbyStoresWithProductName(@ModelAttribute NearbyStoreSearchParams params,
                                                                           @RequestParam String productName) {
        log.info("""
                Searching nearby stores with product name '{}' at lat: {}, long: {}, radius: {} km
                """,
                productName, params.getLatitude(), params.getLongitude(), params.getRadiusInKm());
        PagedResponseDto<NearbyStoreResponseDto> results = nearbyStoreService.findNearbyStoresWithProductName(params, productName);
        log.info("Found {} store(s) with product name '{}' nearby", results.getTotalElements(), productName);
        return ResponseEntity
                .ok(new ApiResponseBody("Nearby Stores with product Name in stock fetched successfully", results));
    }

    @Operation(
            summary = "Searching stores using full test search",
            description = """
                    Retrieves a list stores matching the query provide
                    """,
            parameters = {
                    @Parameter(name = "query", description = "text to search with")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    )
            }
    )
    @GetMapping("/search/fts")
    public ResponseEntity<ApiResponseBody> fullTextSearch(@RequestParam String query) {
        log.info("Searching for store with query {}", query);
        List<NearbyStoreResponseDto> results = storeService.searchByFullTextSearch(query);
        log.info("Found {} store(S) matching query: {} ", results.size(), query);
        return ResponseEntity
                .ok(new ApiResponseBody("Search results: ", results));
    }



    @Operation(
            summary = "Search nearby stores using full-text search and geo-location",
            description = """
                This endpoint combines Full-Text Search (FTS) and geospatial filtering to find stores that:
                - Have matching keywords (e.g. store or product names)
                - Are within the given radius (in kilometers)
                - Have products in stock and active
                Results are sorted by text relevance and nearest distance.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stores found matching query",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No matching stores found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/search/nearby/fts")
    public ResponseEntity<ApiResponseBody> searchNearbyStoresUsingFts(@RequestParam String query,
                                                                      @RequestParam double lat,
                                                                      @RequestParam double lon,
                                                                      @RequestParam double radiusKm ){
        List<NearbyStoreResponseDto> results = nearbyStoreService.searchNearbyWithFullTextSearchAndProductInStock(query, lat, lon, radiusKm);
        return ResponseEntity
                .ok(new ApiResponseBody("Search nearby stores using FTS results: ", results));
    }

    @Operation(
            summary = "Find nearby stores with a specific product ID in stock",
            description = "Retrieves a list of stores located within a specified radius that have a product with the given ID in stock.",
            parameters = {
                    @Parameter(name = "latitude", description = "Current latitude of the user", required = true, example = "34.0522"),
                    @Parameter(name = "longitude", description = "Current longitude of the user", required = true, example = "-118.2437"),
                    @Parameter(name = "radiusInKm", description = "Search radius in kilometers", required = true, example = "10.0"),
                    @Parameter(name = "productId", description = "The ID of the product to check availability for", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nearby stores with the specified product ID fetched successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid coordinates, radius, or product ID."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - No nearby stores found with the product ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during search."
                    )
            }
    )
    @GetMapping("/product/{productId}/nearby")
    public ResponseEntity<ApiResponseBody> findNearbyStoresWithProductId(@RequestParam double latitude,
                                                                         @RequestParam double longitude,
                                                                         @RequestParam double radiusInKm,
                                                                         @PathVariable Long productId) {
        log.info("Searching nearby stores with productId: {} at lat: {}, long: {}, radius: {} km",
                productId, latitude, longitude, radiusInKm);
        List<NearbyStoreResponseDto> results = nearbyStoreService.findNearbyStoresByProductId(latitude, longitude, radiusInKm, productId);
        log.info("Found {} nearby store(s) with productId: {}", results.size(), productId);
        return ResponseEntity.ok(new ApiResponseBody(
                format("Nearby Stores within range %.0f km With Product ID: %d fetched successfully !", radiusInKm, productId),
                results));
    }

}
