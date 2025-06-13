package com.ahmad.ProductFinder.service.productService;

import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ProductResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Product;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDto createProduct(CreateProductRequestDto dto) {
        log.info("createProduct service method invoked");

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(dto.category());

        productRepository.save(product);
        log.info("Product created successfully with name: {}", product.getName());

        return ProductResponseDto.from(product);
    }

    @Transactional
    @Override
    public ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request) {
        log.info("updateProduct service method invoked for ID: {}", productId);

        Product result = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("No product found with Id: {}", productId);
                    return new ResourceNotFoundException("No product found with ID : " + productId);
                });

        result.setName(request.name());
        result.setDescription(request.description());
        result.setPrice(request.price());
        result.setCategory(request.category());
        result.setUpdatedAt(LocalDateTime.now());
        productRepository.save(result);

        log.info("Product with ID {} updated successfully", productId);

        return ProductResponseDto.from(result);
    }

    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        log.info("deleteProduct service method invoked for ID: {}", productId);

        Product result = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("No product found with ID: {}", productId);
                    return new ResourceNotFoundException("No product found with ID : " + productId);
                });

        productRepository.delete(result);
        log.info("Product with ID {} deleted successfully", productId);
    }

    @Override
    public ProductResponseDto getProductById(Long productId) {
        log.info("getProductById service method invoked for ID: {}", productId);

        Product result = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("No product found with ID: {}", productId);
                    return new ResourceNotFoundException("No product found with ID : " + productId);
                });

        return new ProductResponseDto(
                result.getId(),
                result.getName(),
                result.getDescription(),
                result.getPrice(),
                result.getCategory(),
                result.getCreatedAt()
        );
    }

    @Override
    public List<ProductResponseDto> getProductByCategory(String category) {
        log.info("getProductByCategory service method invoked for category: {}", category);

        List<Product> result = productRepository.getProductByCategory(category);
        if (result.isEmpty()){
            log.warn("failed to get product with category: {} , result might be empty",category);
            throw new ResourceNotFoundException("No product found with category : " + category);
        }
        return result.stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        log.info("getAllProducts service method invoked");

        return productRepository.findAll()
                .stream()
                .map(ProductResponseDto::from)
                .toList();

    }

    @Override
    public List<ProductResponseDto> searchProductsByName(String name) {
        log.info("searchProductsByName service method invoked for name: {}", name);

        return productRepository.searchDistinctProductByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    @Override
    public List<ProductResponseDto> filterProductsByPriceRange(BigDecimal min, BigDecimal max) {
        log.info("filterProductsByPriceRange service method invoked for range: {} - {}", min, max);
        if (min.compareTo(max) > 0) {
            log.warn("Invalid price range: min > max");
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price, please review the price range.");
        }
        return productRepository.findByPriceBetween(min, max)
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }
}
