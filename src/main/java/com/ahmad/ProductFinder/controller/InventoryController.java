package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.InventoryResponseDto;
import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.service.inventoryService.IInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseBody> createInventory(@Valid @RequestBody CreateInventoryRequestDto request) {
        inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseBody("Inventory created successfully ", null));
    }

    @PutMapping("/update/{inventoryId}")
    public ResponseEntity<ApiResponseBody> updateInventory(@PathVariable Long inventoryId,
                                                           @Valid @RequestBody UpdateInventoryRequestDto request) {
        Inventory updatedInventory = inventoryService.updateInventory(inventoryId, request);
        return ResponseEntity.ok(new ApiResponseBody("Inventory updated successfully ", updatedInventory));
    }

    @DeleteMapping("/delete/{inventoryId}")
    public ResponseEntity<ApiResponseBody> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.deleteInventoryById(inventoryId);
        return ResponseEntity.ok(new ApiResponseBody("Inventory deleted successfully ", null));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllInventories() {
        return ResponseEntity.ok(
                new ApiResponseBody("List of all active inventories ", inventoryService.getAllInventories(new InventoryResponseDto()))
        );
    }

    @GetMapping("/by-store/{storeId}")
    public ResponseEntity<ApiResponseBody> getInventoryByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories for store, " + storeId + " : ", inventoryService.getInventoryByStore(storeId))
        );
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<ApiResponseBody> getInventoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories for this product with ID " + productId +" : ", inventoryService.getInventoryByProduct(productId))
        );
    }

    @GetMapping("/stock-level")
    public ResponseEntity<ApiResponseBody> getProductStockLevel(@RequestParam Long storeId,
                                                                @RequestParam Long productId) {
        return ResponseEntity.ok(
                new ApiResponseBody("Stock count for this product in this store", inventoryService.getProductsStockLevel(storeId, productId))
        );
    }

    @GetMapping("/by-price")
    public ResponseEntity<ApiResponseBody> getInventoryByPriceRange(@RequestParam BigDecimal min,
                                                                    @RequestParam BigDecimal max) {
        return ResponseEntity.ok(
                new ApiResponseBody("Inventories within price range "+ min + " and " + max +" : ", inventoryService.getInventoryForAProductWithinPriceRange(min, max))
        );
    }

    @GetMapping("/stores-with-product/{productId}")
    public ResponseEntity<ApiResponseBody> getStoresWhereProductAvailable(@PathVariable Long productId) {
        return ResponseEntity.ok(
                new ApiResponseBody("Stores with available product : ", inventoryService.getStoreWhereSpecificProductAvailable(productId))
        );
    }
}
