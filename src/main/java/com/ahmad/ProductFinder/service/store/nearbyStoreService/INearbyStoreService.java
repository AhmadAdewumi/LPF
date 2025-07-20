package com.ahmad.ProductFinder.service.store.nearbyStoreService;

import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.PagedResponseDto;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;

public interface INearbyStoreService {
    PagedResponseDto<NearbyStoreResponseDto> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params);

    PagedResponseDto<NearbyStoreResponseDto> findNearbyStoresWithProductName(NearbyStoreSearchParams params,String productName);

//    List<NearbyStoreResponseDto> searchByFullTextSearch(String query);

    List<NearbyStoreResponseDto> searchNearbyWithFullTextSearchAndProductInStock(String query, double lat, double lon, double radiusInKm);

    List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId);

    List<NearbyStoreResponseDto> findNearbyStoreAndFilterByTags(NearbyStoreSearchParams params, Set<String> tagNames, boolean matchAll);
}
