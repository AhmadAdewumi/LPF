package com.ahmad.ProductFinder.service.productService;

import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ProductResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {
    ProductResponseDto createProduct(CreateProductRequestDto dto);
    ProductResponseDto updateProduct(Long productId, UpdateProductRequestDto dto);
    void deleteProductUsingProductId(Long productId);
    ProductResponseDto getProductUsingProductId(Long productId);
    List<ProductResponseDto> getProductByCategory(String category);
    List<ProductResponseDto> getAllProducts();
    List<ProductResponseDto> searchProductsByProductName(String name);
    List<ProductResponseDto> filterProductsByPriceRange(BigDecimal min, BigDecimal max);
}
