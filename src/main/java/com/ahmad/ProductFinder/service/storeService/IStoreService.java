package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;

import java.util.List;

public interface IStoreService {
    StoreResponseDto createStore(CreateStoreRequestDto dto);

    StoreResponseDto updateStore(Long storeId, UpdateStoreRequestDto dto);

    void deleteStore(Long storeId);

    void disableStore(long storeId);

    StoreResponseDto restoreStore(Long storeId);

    StoreResponseDto getStoreUsingStoreId(Long storeId);

    List<StoreResponseDto> getAllStores();
    // Returns stores within radius from (lat, long)

    List<NearbyStoreResponseDto> findNearbyStores(double latitude, double longitude, double radiusInKm);

    List<NearbyStoreResponseDto> findNearbyStoresWithProductName(double latitude, double longitude, double radiusInKm, String productName);

    List<NearbyStoreResponseDto> searchByFullTextSearch(String query);

    List<NearbyStoreResponseDto> searchNearbyWithByFullTextSearchAndProductInStock(String query, double lat, double lon, double radiusInKm);

    List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId);

    List<StoreWithInventoryDto> searchStoresUsingStoreName(String storeName);




//    //you provide a location with product you want and get the stores that sells the product you requested there
//
//    List<StoreResponseDto> findStoresInLocationWithProductName(double latitude, double longitude, double radiusInKm, String productName);
//
//    List<StoreResponseDto> findStoresInLocationWithProductId(double latitude, double longitude, double radiusInKm, Long productId);
    // Search stores by name and include their inventory
}
