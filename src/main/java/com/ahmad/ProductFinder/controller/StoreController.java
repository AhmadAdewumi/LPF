package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.StoreDocs;
import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.*;
import com.ahmad.ProductFinder.service.store.nearbyStoreService.INearbyStoreService;
import com.ahmad.ProductFinder.service.store.storeService.IStoreService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping("/api/v1/store")
@Tag(name = "Store Management", description = "APIs for creating, updating, retrieving, deleting, and searching stores, including location-based searches and product availability.")
public class StoreController implements StoreDocs {
    private final IStoreService storeService;
    private final INearbyStoreService nearbyStoreService;

    public StoreController(IStoreService storeService, INearbyStoreService nearbyStoreService) {
        this.storeService = storeService;
        this.nearbyStoreService = nearbyStoreService;
    }


    @PreAuthorize("hasAnyRole('USER','STORE_OWNER','ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> createStore(@RequestBody CreateStoreRequestDto request) {
        log.info("Creating store with name: {}", request.name());
        StoreResponseDto result = storeService.createStore(request);
        log.info("Store created successfully with ID: {}", result.id());
        return ResponseEntity.ok(new ApiResponseBody("Store created successfully", result));
    }

    @PreAuthorize("hasRole('STORE_OWNER')")
    @PatchMapping(value = "/update/{storeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateStore(@PathVariable Long storeId,
                                                       @RequestBody @Valid UpdateStoreRequestDto request) {
        log.info("Updating store with ID: {}", storeId);
        StoreResponseDto updatedStore = storeService.updateStore(storeId, request);
        log.info("Store with ID: {} updated successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store With ID " + storeId + " Updated Successfully !", updatedStore));
    }


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
    public ResponseEntity<ApiResponseBody> restoreStore(@PathVariable Long storeId) {
        log.info("Restore Store endpoint called");
        StoreResponseDto result = storeService.restoreStore(storeId);
        log.info("Store restored successfully");
        return ResponseEntity.ok(new ApiResponseBody("Store restored Successfully", result));
    }

    @PreAuthorize("hasAnyRole('STORE_OWNER','ADMIN')")
    @DeleteMapping("/delete/{storeId}")
    public ResponseEntity<ApiResponseBody> deleteStoreUsingStoreId(@PathVariable Long storeId) {
        log.info("Deleting store with ID: {}", storeId);
        storeService.deleteStore(storeId);
        log.info("Store with ID: {} deleted successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " deleted successfully !", null));
    }


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


    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponseBody> getStoreById(@PathVariable Long storeId) {
        log.info("Fetching store with ID: {}", storeId);
        StoreResponseDto store = storeService.getStoreUsingStoreId(storeId);
        log.info("Store with ID: {} fetched successfully", storeId);
        return ResponseEntity
                .ok(new ApiResponseBody("Store with ID : " + storeId + " retrieved successfully !", store));
    }

    @GetMapping(value = "/nearby")
    public ResponseEntity<ApiResponseBody> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params) {
        log.info("Searching for nearby stores at lat: {}, long: {} within radius: {} km", params.getLatitude(), params.getLongitude(), params.getRadiusInKm());
        PagedResponseDto<NearbyStoreResponseDto> results = nearbyStoreService.findNearbyStores(params);
        log.info("Found {} nearby store(s)", results.getTotalElements());
        return ResponseEntity.ok(new ApiResponseBody("Nearby Stores Fetched Successfully ! ", results));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchStoresByStoreName(@RequestParam String storeName) {
        log.info("Searching stores with name like: {}", storeName);
        List<StoreWithInventoryDto> results = storeService.searchStoresUsingStoreName(storeName);
        log.info("Found {} store(s) matching '{}'", results.size(), storeName);
        return ResponseEntity
                .ok(new ApiResponseBody(format("Stores matching value,'%s' , provided successfully !", storeName), results));
    }


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


    @GetMapping("/search/fts")
    public ResponseEntity<ApiResponseBody> fullTextSearch(@RequestParam String query) {
        log.info("Searching for store with query {}", query);
        List<NearbyStoreResponseDto> results = storeService.searchByFullTextSearch(query);
        log.info("Found {} store(S) matching query: {} ", results.size(), query);
        return ResponseEntity
                .ok(new ApiResponseBody("Search results: ", results));
    }


    @GetMapping("/search/nearby/fts")
    public ResponseEntity<ApiResponseBody> searchNearbyStoresUsingFts(@RequestParam String query,
                                                                      @RequestParam double lat,
                                                                      @RequestParam double lon,
                                                                      @RequestParam double radiusKm) {
        List<NearbyStoreResponseDto> results = nearbyStoreService.searchNearbyWithFullTextSearchAndProductInStock(query, lat, lon, radiusKm);
        return ResponseEntity
                .ok(new ApiResponseBody("Search nearby stores using FTS results: ", results));
    }


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

    @PostMapping("/{storeId}/tags")
    public ResponseEntity<ApiResponseBody> assignTagsToStore(@PathVariable Long storeId,
                                                             @RequestBody Collection<String> tagNames) {
        log.info("Assigning tags to store ID: {}", storeId);
        storeService.assignTagsToStore(storeId, tagNames);
        return ResponseEntity.ok(new ApiResponseBody("Tags assigned successfully!", null));
    }

    @DeleteMapping("/{storeId}/tags")
    public ResponseEntity<ApiResponseBody> removeTagFromStore(@PathVariable Long storeId,
                                                              @RequestParam String tagName) {
        log.info("Removing tag '{}' from store ID: {}", tagName, storeId);
        storeService.removeTagsFromStore(storeId, tagName);
        return ResponseEntity.ok(new ApiResponseBody("Tag removed successfully!", null));
    }

    @GetMapping("/search/by-tags")
    public ResponseEntity<ApiResponseBody> findStoresByTags(@RequestParam Set<String> tags,
                                                            @RequestParam boolean matchAll,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "name") String sortBy,
                                                            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        PagedResponseDto<StoreResponseDto> results = storeService.findStoresByTags(tags, matchAll, pageable);
        return ResponseEntity.ok(new ApiResponseBody("Stores with specified tags fetched successfully!", results));
    }

    @PostMapping("/search/nearby/by-tags")
    public ResponseEntity<ApiResponseBody> findNearbyStoresByTags(@ModelAttribute NearbyStoreSearchParams params,
                                                                  @RequestParam Set<String> tags,
                                                                  @RequestParam boolean matchAll) {
        log.info("Tags received: {}", tags);
        tags.forEach(tag -> log.info("tag: '{}'", tag));
        List<NearbyStoreResponseDto> results = nearbyStoreService.findNearbyStoreAndFilterByTags(params, tags, matchAll);
        return ResponseEntity.ok(new ApiResponseBody("Nearby Stores filtered by tags fetched successfully!", results));
    }

}
