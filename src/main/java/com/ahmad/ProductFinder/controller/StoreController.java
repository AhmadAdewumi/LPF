package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;
import com.ahmad.ProductFinder.service.storeService.IStoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store")
public class StoreController {
    private final IStoreService storeService;

    public StoreController(IStoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseBody> createStore() {
        return null;
    }

    @PutMapping("/update/{storeId}")
    public ResponseEntity<ApiResponseBody> updateStore(@PathVariable Long storeId,
                                                       @RequestBody @Valid UpdateStoreRequestDto request) {
        StoreResponseDto updatedStore = storeService.updateStore(storeId, request);
        return ResponseEntity
                .ok(new ApiResponseBody("Store With ID " + storeId + "Updated Successfully !", updatedStore));
    }

    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ApiResponseBody> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " deleted successfully !", null));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllStores() {
        List<StoreResponseDto> stores = storeService.getAllStores();
        return ResponseEntity
                .ok(new ApiResponseBody("All stores retrieved successfully !", stores));
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponseBody> getStoreById(@PathVariable Long storeId) {
        StoreResponseDto store = storeService.getStoreById(storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " retrieved successfully !", store));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponseBody> findNearbyStores(@RequestParam double latitude,
                                                            @RequestParam double longitude,
                                                            @RequestParam double radiusInKm) {
        List<NearbyStoreResponseDto> results = storeService.findNearbyStores(latitude, longitude, radiusInKm);
        return ResponseEntity.ok(new ApiResponseBody("Nearby Store Fetched Successfully ! ", results));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchStoresByName(@RequestParam String storeName) {
        List<StoreWithInventoryDto> results = storeService.searchStoresByName(storeName);
        return ResponseEntity
                .ok(new ApiResponseBody("Stores matching name provided successfully !", results));
    }

    @GetMapping("/product/search")
    public ResponseEntity<ApiResponseBody> findNearbyStoresWithProductName(@RequestParam double latitude,
                                                                           @RequestParam double longitude,
                                                                           @RequestParam double radiusInKm,
                                                                           @RequestParam String productName) {
        List<NearbyStoreResponseDto> results = storeService
                .findNearbyStoresWithProductName(latitude, longitude, radiusInKm, productName);
        return ResponseEntity
                .ok(new ApiResponseBody("Nearby Stores with product Name in stock fetched successfully" , results));
    }

    @GetMapping("/product/{productId}/nearby")
    public ResponseEntity<ApiResponseBody> findNearbyStoresWithProductId(@RequestParam double latitude,
                                                                         @RequestParam double longitude,
                                                                         @RequestParam double radiusInKm,
                                                                         @PathVariable Long productId){
        List<NearbyStoreResponseDto> results = storeService
                .findNearbyStoresWithProductId(latitude, longitude, radiusInKm, productId);
        return ResponseEntity.ok(new ApiResponseBody("Nearby Stores With Product ID fetched auccessfully ! " ,results));
    }
}
