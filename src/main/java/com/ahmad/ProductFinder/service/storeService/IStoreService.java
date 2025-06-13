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

    void deleteStore(long storeId);

    StoreResponseDto getStoreById(Long storeId);

    List<StoreResponseDto> getAllStores();
    // Returns stores within radius from (lat, long)

    List<NearbyStoreResponseDto> findNearbyStores(double latitude, double longitude, double radiusInKm);

    List<NearbyStoreResponseDto> findNearbyStoresWithProductName(double latitude, double longitude, double radiusInKm, String productName);

    List<NearbyStoreResponseDto> findNearbyStoresWithProductId(double latitude, double longitude, double radiusInKm, Long productId);

    List<StoreWithInventoryDto> searchStoresByName(String storeName);




//    //you provide a location with product you want and get the stores that sells the product you requested there
//
//    List<StoreResponseDto> findStoresInLocationWithProductName(double latitude, double longitude, double radiusInKm, String productName);
//
//    List<StoreResponseDto> findStoresInLocationWithProductId(double latitude, double longitude, double radiusInKm, Long productId);
    // Search stores by name and include their inventory
}
