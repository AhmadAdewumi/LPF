package com.ahmad.ProductFinder.service.store.nearbyStoreService;

import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.PagedResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.projection.StoreProjection;
import com.ahmad.ProductFinder.service.store.storeService.StoreMapper;
import com.ahmad.ProductFinder.service.store.storeService.StoreQueryService;
import com.ahmad.ProductFinder.service.store.utils.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Slf4j
@Service
public class NearbyStoreService implements INearbyStoreService {

    private final StoreQueryService storeQueryService;
    private final StoreMapper storeMapper;
    private final StoreUtils storeUtils;

    public NearbyStoreService(StoreQueryService storeQueryService, StoreMapper storeMapper, StoreUtils storeUtils) {
        this.storeQueryService = storeQueryService;
        this.storeMapper = storeMapper;
        this.storeUtils = storeUtils;
    }

    @Override
    public PagedResponseDto<NearbyStoreResponseDto> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params) {
        log.info("Searching nearby stores within {} km of (lat={}, lon={})", params.getRadiusInKm(), params.getLatitude(), params.getLongitude());

        Pageable pageable = buildPageable(params.getPage(), params.getSize(), params.getSortBy(), params.getDirection());
        double radiusInMetres = convertKmToMetres(params.getRadiusInKm());

        Page<StoreProjection> resultPage = storeQueryService.retrieveNearbyStores(params.getLatitude(), params.getLongitude(), radiusInMetres, pageable);

        if (resultPage.isEmpty()) {
            log.warn("No nearby stores found within radius {} km", params.getRadiusInKm());
            throw new ResourceNotFoundException("No stores found within " + params.getRadiusInKm() + "km of your location !");
        }

        return storeMapper.toPagedResponseDto(resultPage);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResponseDto<NearbyStoreResponseDto> findNearbyStoresWithProductName(NearbyStoreSearchParams params, String productName) {
        log.info("Searching nearby stores with product '{}' within {} km", productName, params.getRadiusInKm());

        Pageable pageable = buildPageable(params.getPage(), params.getSize(), params.getSortBy(), params.getDirection());
        double radiusInMetres = convertKmToMetres(params.getRadiusInKm());

        Page<StoreProjection> resultPage = storeQueryService.searchNearbyStoresWithProductName(params.getLatitude(), params.getLongitude(), radiusInMetres, pageable, productName);

        if (resultPage.isEmpty()) {
            log.warn("No nearby stores found with product: {}", productName);
            throw new ResourceNotFoundException("No product found with name : " + productName);
        }

        return storeMapper.toPagedResponseDto(resultPage);
    }

    @Override
    public List<NearbyStoreResponseDto> searchNearbyWithFullTextSearchAndProductInStock(String query, double lat, double lon, double radiusInKm) {
        log.info("FTS Nearby store search | Query: '{}' | Lat: {} | Lon: {} | Radius: {}km", query, lat, lon, radiusInKm);

        double radiusInMetres = convertKmToMetres(radiusInKm);

        List<StoreProjection> results = storeQueryService.searchNearbyWithByFullTextSearchAndProductInStock(query, lat, lon, radiusInMetres);

        if (results.isEmpty()) {
            log.warn("No FTS match for: {}", query);
            throw new ResourceNotFoundException("No Nearby stores matching: " + query);
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    public List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId) {
        log.info("Nearby stores with product ID {} within {} km", productId, radiusInKm);

        double radiusInMetres = convertKmToMetres(radiusInKm);

        List<StoreProjection> results = storeQueryService.searchNearbyStoresWithProductId(latitude, longitude, radiusInMetres, productId);

        if (results.isEmpty()) {
            log.warn("No stores within {} km found with product ID: {}", radiusInKm, productId);
            throw new ResourceNotFoundException(format("Oops, No nearby stores within radius, %.0f km , have that product in stock", radiusInKm));
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    public List<NearbyStoreResponseDto> findNearbyStoreAndFilterByTags(NearbyStoreSearchParams params, Set<String> tagNames, boolean matchAll) {
        List<NearbyStoreResponseDto> nearbyStores = findNearbyStores(params).getContent();

        if (tagNames == null || tagNames.isEmpty()) {
            return nearbyStores;
        }

        Set<String> normalizedTagNames = storeUtils.normalizeTagNames(tagNames);

        return nearbyStores.stream().filter(
                store -> {
                    Set<String> storeTags = storeUtils.normalizeTagNames(store.tags());

                    if (matchAll) {
                        return storeTags.containsAll(normalizedTagNames);
                    } else
                        return !Collections.disjoint(storeTags, normalizedTagNames); // the disjoint returns true if the
                    //two sets have no common elements, so it is negated (i.e not completely disjoint)
                }
        ).toList();
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    private double convertKmToMetres(double km) {
        return km * 1000;
    }
}
