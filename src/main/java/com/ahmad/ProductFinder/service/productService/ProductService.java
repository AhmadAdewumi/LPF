package com.ahmad.ProductFinder.service.productService;

import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ProductResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Product;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDto createProduct(CreateProductRequestDto dto) {
        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(dto.category());

        productRepository.save(product);

        return ProductResponseDto.from(product);

    }

    @Override
    public ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto request) {

        Product result = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product found with ID : " + productId));

        result.setName(request.name());
        result.setDescription(request.description());
        result.setPrice(request.price());
        result.setCategory(request.category());
        result.setUpdatedAt(LocalDateTime.now());

        return ProductResponseDto.from(result);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product result = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product found with ID : " + productId));
        productRepository.delete(result);
    }

    @Override
    public ProductResponseDto getProductById(Long productId) {
        Product result = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product found with ID : " + productId));
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
        List<Product> result = productRepository.getProductByCategory(category)
                .orElseThrow(() -> new ResourceNotFoundException("No product found with category : " + category));
        return result.stream()
                .map(ProductResponseDto::from)
                .toList();

    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    @Override
    public List<ProductResponseDto> searchProductsByName(String name) {
        return productRepository.searchDistinctProductByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }

    @Override
    public List<ProductResponseDto> filterProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max)
                .stream()
                .map(ProductResponseDto::from)
                .toList();
    }
}
