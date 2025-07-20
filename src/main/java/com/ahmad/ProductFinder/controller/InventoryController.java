package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.InventoryDocs;
import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.service.inventoryService.IInventoryService;
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
public class InventoryController implements InventoryDocs{

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> createInventory(@Valid @RequestBody CreateInventoryRequestDto request) {
        log.info("Received request to create inventory for productId: {}, storeId: {}", request.getProductId(), request.getStoreId());
        Inventory result = inventoryService.createInventory(request);
        log.info("Inventory created successfully with ID: {}", result.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseBody("Inventory created successfully ", result));
    }


    @PatchMapping(value = "/update/{inventoryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateInventoryUsingInventoryId(@PathVariable Long inventoryId, @Valid @RequestBody UpdateInventoryRequestDto request) {
        log.info("Received request to update inventory with ID: {}", inventoryId);
        Inventory updatedInventory = inventoryService.updateInventoryByInventoryId(inventoryId, request);
        log.info("Inventory with ID: {} updated successfully", inventoryId);
        return ResponseEntity.ok(new ApiResponseBody("Inventory updated successfully ", updatedInventory));
    }

    @DeleteMapping("/delete/{inventoryId}")
    public ResponseEntity<ApiResponseBody> deleteInventoryUsingInventoryId(@PathVariable Long inventoryId) {
        log.info("Request to delete inventory with ID: {}", inventoryId);
        inventoryService.deleteInventoryByInventoryId(inventoryId);
        log.info("Inventory with ID: {} deleted successfully", inventoryId);
        return ResponseEntity.ok(new ApiResponseBody("Inventory deleted successfully ", null));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllInventories() {
        log.info("Fetching all active inventories");
        var results = inventoryService.getAllActiveInventories();
        log.info("Fetched {} inventory record(s)", results.size());
        return ResponseEntity.ok(
                new ApiResponseBody("List of all active inventories ", results)
        );
    }


    @GetMapping("/by-store/{storeId}")
    public ResponseEntity<ApiResponseBody> getInventoryUsingStoreId(@PathVariable Long storeId) {
        log.info("Fetching inventory for storeId: {}", storeId);
        var results = inventoryService.getInventoryUsingStoreId(storeId);
        log.info("Fetched {} inventory record(s) for storeId: {}", results.size(), storeId);
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories for store, " + storeId + " : ", results)
        );
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<ApiResponseBody> getInventoryUsingProductId(@PathVariable Long productId) {
        log.info("Fetching inventory for productId: {}", productId);
        var results = inventoryService.getInventoryUsingProductId(productId);
        log.info("Fetched {} inventory record(s) for productId: {}", results.size(), productId);
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories for this product with ID " + productId + " : ", results)
        );
    }


    @GetMapping("/stock-level")
    public ResponseEntity<ApiResponseBody> getProductStockLevel(@RequestParam Long storeId, @RequestParam Long productId) {
        log.info("Fetching stock level for productId: {} in storeId: {}", productId, storeId);
        var stock = inventoryService.getProductsStockLevel(storeId, productId);
        log.info("Stock level for productId: {} in storeId: {} is {}", productId, storeId, stock);
        return ResponseEntity.ok(
                new ApiResponseBody("Stock count for this product in this store", stock)
        );
    }

    @GetMapping("/by-price")
    public ResponseEntity<ApiResponseBody> getInventoryByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        log.info("Fetching inventories within price range: {} - {}", min, max);
        var results = inventoryService.getInventoryForProductWithinPriceRange(min, max);
        log.info("Fetched {} inventory record(s) within price range: {} - {}", results.size(), min, max);
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories within price range " + min + " and " + max + " : ", results)
        );
    }


    @GetMapping("/stores-with-product/{productId}")
    public ResponseEntity<ApiResponseBody> getStoresWithProductInStockUsingProductId(@PathVariable Long productId) {
        log.info("Fetching stores where productId: {} is available", productId);
        var results = inventoryService.getStoresWithProductInStock(productId);
        log.info("Found {} store(s) with productId: {}", results.size(), productId);
        return ResponseEntity.ok(
                new ApiResponseBody("Stores with available product : ", results)
        );
    }
}