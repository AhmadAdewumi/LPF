package com.ahmad.ProductFinder.repositories;

import com.ahmad.ProductFinder.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory , Long> {
    //gets product in a store and ensure that the store is active
    List<Inventory> findInventoryByStoreIdAndIsActiveTrueOrderByPrice(Long storeId);

    //gets stores that have the product in stock
    @Query("SELECT i from Inventory i WHERE i.product.id= :productId AND i.isActive=true AND i.stockQuantity>0")
    List<Inventory> findInventoryByProductIdAndQuantityGreaterThan(@Param("productId") Long productId);

    @Query("SELECT SUM(i.stockQuantity) FROM Inventory i WHERE i.store.id = :storeId AND i.product.id = :productId AND i.isActive = true")
    Integer countStockForProductInStore(@Param("storeId") Long storeId, @Param("productId") Long productId);


    //this gets the quantity of product left in a specific store NB: use getQuantity in the service layer to return the quantity
    //instead of row
    Optional<Inventory> findInventoryByStoreIdAndProductId(Long storeId , Long ProductId);

    //filters store by price range
    List<Inventory> findByPriceBetweenAndIsActiveTrueOrderByPriceAsc(BigDecimal minimumPrice, BigDecimal maximumPrice);

    boolean existsByStoreIdAndProductId(Long storeId,Long productId);

    List<Inventory> findAllByIsActiveTrue();

    List<Inventory> findByProduct_IdAndIsActiveIsTrueAndStockQuantityGreaterThan(Long productId , int minStockQuantity);
}
