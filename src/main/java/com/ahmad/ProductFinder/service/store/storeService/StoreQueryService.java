    package com.ahmad.ProductFinder.service.store.storeService;

    import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
    import com.ahmad.ProductFinder.projection.StoreProjection;
    import com.ahmad.ProductFinder.repositories.ProductRepository;
    import com.ahmad.ProductFinder.repositories.StoreRepository;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
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

        public Page<StoreProjection> searchNearbyStoresWithProductName(double latitude, double longitude, double radiusInMetres,Pageable pageable, String productName) {
            productRepository.findByNameIgnoreCase(productName)
                    .orElseThrow(() -> {
                        log.error("Product with name {} not found", productName);
                        return new ResourceNotFoundException(format("No products found with name: %s ", productName));
                    });

            return storeRepository.searchNearbyStoresWithProductName(latitude, longitude, productName, radiusInMetres,pageable);
        }

        public List<StoreProjection> fullTextSearch(String query){
            return storeRepository.searchByText(query);
        }


        public List<StoreProjection> searchNearbyStoresWithProductId(double latitude, double longitude, double radiusInMetres, Long productId) {
            productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.error("Product with ID {} not found", productId);
                        return new ResourceNotFoundException(format("No products found with ID: %d ", productId));
                    });
            return storeRepository.findNearbyStoresWithProductId(latitude, longitude, productId, radiusInMetres).orElse(Collections.emptyList());

        }

        public Page<StoreProjection> retrieveNearbyStores(double latitude, double longitude, double radiusInMetres, Pageable pageable){
            return storeRepository.getNearbyStores(latitude, longitude, radiusInMetres,pageable);
        }

        public List<StoreProjection> searchNearbyWithByFullTextSearchAndProductInStock(String query, double lat, double lon, double distanceKm){
            return storeRepository.searchNearbyStoresByFullTextSearchAndProductInStock(query,lat,lon,distanceKm);
        }
    }
