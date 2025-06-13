package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.service.inventoryService.IInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing product inventory in stores.")
public class InventoryController {

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(
            summary = "Create new inventory record",
            description = "Creates a new inventory record associating a product with a store, including initial quantity and price. " +
                    "Requires valid product and store IDs.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details for creating a new inventory record",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateInventoryRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Inventory created successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, e.g., validation errors for input data, or product/store not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during inventory creation.")
            }
    )
    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> createInventory(@Valid @RequestBody CreateInventoryRequestDto request) {
        log.info("Received request to create inventory for productId: {}, storeId: {}", request.getProductId(), request.getStoreId());
        Inventory result = inventoryService.createInventory(request);
        log.info("Inventory created successfully with ID: {}", result.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseBody("Inventory created successfully ", result));
    }

    @Operation(
            summary = "Update an existing inventory record",
            description = "Updates the details (e.g., stockQuantity, price,isActive) of an existing inventory record using the inventory ID.",
            parameters = {
                    @Parameter(name = "inventoryId", description = "The ID of the inventory record to update", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated inventory details",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateInventoryRequestDto.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inventory updated successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, e.g., validation errors or invalid inventory data."),
                    @ApiResponse(responseCode = "404", description = "Inventory record not found for the given ID."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during inventory update.")
            }
    )
    @PatchMapping(value = "/update/{inventoryId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateInventory(@PathVariable Long inventoryId,
                                                           @Valid @RequestBody UpdateInventoryRequestDto request) {
        log.info("Received request to update inventory with ID: {}", inventoryId);
        Inventory updatedInventory = inventoryService.updateInventory(inventoryId, request);
        log.info("Inventory with ID: {} updated successfully", inventoryId);
        return ResponseEntity.ok(new ApiResponseBody("Inventory updated successfully ", updatedInventory));
    }


    @Operation(
            summary = "Delete an inventory record",
            description = "Deletes an inventory record by its ID.",
            parameters = {
                    @Parameter(name = "inventoryId", description = "The ID of the inventory record to delete", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inventory deleted successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Inventory record not found for the given ID."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during inventory deletion.")
            }
    )
    @DeleteMapping("/delete/{inventoryId}")
    public ResponseEntity<ApiResponseBody> deleteInventory(@PathVariable Long inventoryId) {
        log.info("Request to delete inventory with ID: {}", inventoryId);
        inventoryService.deleteInventoryById(inventoryId);
        log.info("Inventory with ID: {} deleted successfully", inventoryId);
        return ResponseEntity.ok(new ApiResponseBody("Inventory deleted successfully ", null));
    }


    @Operation(
            summary = "Get all inventory records",
            description = "Retrieves a list of all active inventory records in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all active inventories.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error during retrieval.")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllInventories() {
        log.info("Fetching all active inventories");
        var results = inventoryService.getAllInventories();
        log.info("Fetched {} inventory record(s)", results.size());
        return ResponseEntity.ok(
                new ApiResponseBody("List of all active inventories ", results)
        );
    }

    @Operation(
            summary = "Get inventory by store",
            description = "Retrieves all inventory records for a specific store using the store ID.",
            parameters = {
                    @Parameter(name = "storeId", description = "The ID of the store", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved inventories for the specified store.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Store not found or no inventories for the store."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during retrieval.")
            }
    )
    @GetMapping("/by-store/{storeId}")
    public ResponseEntity<ApiResponseBody> getInventoryByStore(@PathVariable Long storeId) {
        log.info("Fetching inventory for storeId: {}", storeId);
        var results = inventoryService.getInventoryByStore(storeId);
        log.info("Fetched {} inventory record(s) for storeId: {}", results.size(), storeId);
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories for store, " + storeId + " : ", results)
        );
    }

    @Operation(
            summary = "Get inventory by product",
            description = "Retrieves all inventory records for a specific product using the product ID.",
            parameters = {
                    @Parameter(name = "productId", description = "The ID of the product", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved inventories for the specified store.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Store not found or no inventories for the store."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during retrieval.")
            }
    )
    @GetMapping("/by-product/{productId}")
    public ResponseEntity<ApiResponseBody> getInventoryByProduct(@PathVariable Long productId) {
        log.info("Fetching inventory for productId: {}", productId);
        var results = inventoryService.getInventoryByProduct(productId);
        log.info("Fetched {} inventory record(s) for productId: {}", results.size(), productId);
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories for this product with ID " + productId + " : ", results)
        );
    }

    @Operation(
            summary = "Get product stock level in a specific store",
            description = "Retrieves the stock level of a given product in a specific store.",
            parameters = {
                    @Parameter(name = "storeId", description = "The ID of the store", required = true, example = "1"),
                    @Parameter(name = "productId", description = "The ID of the product", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved stock count for the product in the store.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Product or store not found, or inventory record not found."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during retrieval.")
            }
    )
    @GetMapping("/stock-level")
    public ResponseEntity<ApiResponseBody> getProductStockLevel(@RequestParam Long storeId, @RequestParam Long productId) {
        log.info("Fetching stock level for productId: {} in storeId: {}", productId, storeId);
        var stock = inventoryService.getProductsStockLevel(storeId, productId);
        log.info("Stock level for productId: {} in storeId: {} is {}", productId, storeId, stock);
        return ResponseEntity.ok(
                new ApiResponseBody("Stock count for this product in this store", stock)
        );
    }

    @Operation(
            summary = "Get inventory by price range",
            description = "Retrieves all inventory records where the product's price falls within the specified minimum and maximum range.",
            parameters = {
                    @Parameter(name = "min", description = "Minimum price for filtering", required = true, example = "10.00"),
                    @Parameter(name = "max", description = "Maximum price for filtering", required = true, example = "100.00")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved inventories within the price range.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, e.g., invalid price range."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during retrieval.")
            }
    )
    @GetMapping("/by-price")
    public ResponseEntity<ApiResponseBody> getInventoryByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        log.info("Fetching inventories within price range: {} - {}", min, max);
        var results = inventoryService.getInventoryForAProductWithinPriceRange(min, max);
        log.info("Fetched {} inventory record(s) within price range: {} - {}", results.size(), min, max);
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories within price range " + min + " and " + max + " : ", results)
        );
    }

    @Operation(
            summary = "Get stores where a specific product is available",
            description = "Retrieves a list of stores where a product with the given ID is currently in stock.",
            parameters = {
                    @Parameter(name = "productId", description = "The ID of the product to check availability for", required = true, example = "101")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved stores where the product is available.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found or not available in any store."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during retrieval.")
            }
    )
    @GetMapping("/stores-with-product/{productId}")
    public ResponseEntity<ApiResponseBody> getStoresWhereProductAvailable(@PathVariable Long productId) {
        log.info("Fetching stores where productId: {} is available", productId);
        var results = inventoryService.getStoreWhereSpecificProductAvailable(productId);
        log.info("Found {} store(s) with productId: {}", results.size(), productId);
        return ResponseEntity.ok(
                new ApiResponseBody("Stores with available product : ", results)
        );
    }
}