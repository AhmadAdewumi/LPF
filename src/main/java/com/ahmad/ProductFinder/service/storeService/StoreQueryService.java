package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.projection.StoreProjection;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
public class StoreQueryService {
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public StoreQueryService(StoreRepository storeRepository, ProductRepository productRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    public List<StoreProjection> searchNearbyStoresWithProductName(double latitude, double longitude, double radiusInMetres, String productName) {
        productRepository.findByNameIgnoreCase(productName)
                .orElseThrow(() -> {
                    log.error("Product with name {} not found", productName);
                    return new ResourceNotFoundException(format("No products found with name: %s ", productName));
                });

        return storeRepository.searchNearbyStoresWithProductName(latitude, longitude, productName, radiusInMetres).orElse(Collections.emptyList());
    }

    public List<StoreProjection> searchNearbyStoresWithProductId(double latitude, double longitude, double radiusInMetres, Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product with ID {} not found", productId);
                    return new ResourceNotFoundException(format("No products found with ID: %d ", productId));
                });
        return storeRepository.findNearbyStoresWithProductId(latitude, longitude, productId, radiusInMetres).orElse(Collections.emptyList());

    }

    public List<StoreProjection> retrieveNearbyStores(double latitude, double longitude, double radiusInMetres){
        return storeRepository.getNearbyStores(latitude, longitude, radiusInMetres).orElse(Collections.emptyList());
    }

}
