package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.PagedResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IStoreService {
    StoreResponseDto createStore(CreateStoreRequestDto dto);

    StoreResponseDto updateStore(Long storeId, UpdateStoreRequestDto dto);

    void deleteStore(Long storeId);

    void disableStore(long storeId);

    StoreResponseDto restoreStore(Long storeId);

    StoreResponseDto getStoreUsingStoreId(Long storeId);

    PagedResponseDto<StoreResponseDto> getAllStores(int page, int size, String sortBy, String direction);
    // Returns stores within radius from (lat, long)

    PagedResponseDto<NearbyStoreResponseDto> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params);

    PagedResponseDto<NearbyStoreResponseDto> findNearbyStoresWithProductName(NearbyStoreSearchParams params,String productName);

    List<NearbyStoreResponseDto> searchByFullTextSearch(String query);

    List<NearbyStoreResponseDto> searchNearbyWithByFullTextSearchAndProductInStock(String query, double lat, double lon, double radiusInKm);

    List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId);

    List<StoreWithInventoryDto> searchStoresUsingStoreName(String storeName);

    void assignTagsToStore(Long storeId, Collection<String> tagNames);

    void removeTagsFromStore(Long storeId , String tagName);

    PagedResponseDto<StoreResponseDto> findStoresByTags(Set<String> tagNames, boolean matchAll, Pageable pageable);


//    //you provide a location with product you want and get the stores that sells the product you requested there
//
//    List<StoreResponseDto> findStoresInLocationWithProductName(double latitude, double longitude, double radiusInKm, String productName);
//
//    List<StoreResponseDto> findStoresInLocationWithProductId(double latitude, double longitude, double radiusInKm, Long productId);
    // Search stores by name and include their inventory
}
