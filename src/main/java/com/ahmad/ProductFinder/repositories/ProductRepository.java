package com.ahmad.ProductFinder.repositories;

import com.ahmad.ProductFinder.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsById(Long productId);

    Optional<List<Product>> getProductByCategory(String category);

    List<Product> searchDistinctProductByNameContainingIgnoreCase(String name);

    List<Product> findByPriceBetween(BigDecimal minPrice,BigDecimal maxPrice);
}
