package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@SecurityRequirement(name = "bearerAuth")
@Hidden
public interface InventoryDocs {

    @Operation(
            summary = "Create inventory",
            description = "Creates a new inventory record linking a product to a store with stock and price.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateInventoryRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Inventory created.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input or product/store not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> createInventory(@Valid @RequestBody CreateInventoryRequestDto request);

    @Operation(
            summary = "Update inventory",
            description = "Updates stock, price, or status for an existing inventory record.",
            parameters = {
                    @Parameter(name = "inventoryId", description = "Inventory ID", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateInventoryRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory updated.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input."),
                    @ApiResponse(responseCode = "404", description = "Inventory not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> updateInventoryUsingInventoryId(
            @PathVariable Long inventoryId,
            @Valid @RequestBody UpdateInventoryRequestDto request
    );

    @Operation(
            summary = "Delete inventory",
            description = "Deletes an inventory record by ID.",
            parameters = {
                    @Parameter(name = "inventoryId", description = "Inventory ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory deleted.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Inventory not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> deleteInventoryUsingInventoryId(@PathVariable Long inventoryId);

    @Operation(
            summary = "Get all inventories",
            description = "Retrieves all active inventory records.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventories retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getAllInventories();

    @Operation(
            summary = "Get inventory by store ID",
            description = "Fetches inventory for a specific store.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Store not found or no inventory."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getInventoryUsingStoreId(@PathVariable Long storeId);

    @Operation(
            summary = "Get inventory by product ID",
            description = "Fetches inventory for a specific product.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found or no inventory."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getInventoryUsingProductId(@PathVariable Long productId);

    @Operation(
            summary = "Get product stock in store",
            description = "Returns stock quantity of a product in a specific store.",
            parameters = {
                    @Parameter(name = "storeId", description = "Store ID", required = true, example = "1"),
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock level retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Inventory not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getProductStockLevel(@RequestParam Long storeId, @RequestParam Long productId);

    @Operation(
            summary = "Filter inventory by price range",
            description = "Fetches inventory records where product price is between the given min and max.",
            parameters = {
                    @Parameter(name = "min", description = "Minimum price", required = true, example = "10.00"),
                    @Parameter(name = "max", description = "Maximum price", required = true, example = "100.00")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered inventory retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid price range."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getInventoryByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max);

    @Operation(
            summary = "Get stores where product is available",
            description = "Finds all stores where a product is in stock.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "101")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stores retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found or not in stock."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getStoresWithProductInStockUsingProductId(@PathVariable Long productId);
}
