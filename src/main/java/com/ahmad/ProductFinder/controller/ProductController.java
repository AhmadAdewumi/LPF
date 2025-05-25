package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.ProductResponseDto;
import com.ahmad.ProductFinder.service.productService.IProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponseBody> createProduct(@RequestBody @Valid CreateProductRequestDto request){
        ProductResponseDto result = productService.createProduct(request);
        return ResponseEntity.ok(new ApiResponseBody("Product Created Successfully" , result));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponseBody> updateStore(@PathVariable Long productId,
                                                       @RequestBody @Valid UpdateProductRequestDto request) {
        ProductResponseDto updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity
                .ok(new ApiResponseBody("Product With ID " + productId + "Updated Successfully !", updatedProduct));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseBody> getProductById(@PathVariable Long productId) {
        ProductResponseDto result = productService.getProductById(productId);
        return ResponseEntity
                .ok(new ApiResponseBody("Product with ID : " + productId + " retrieved successfully !", result));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponseBody> deleteStore(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity
                .ok(new ApiResponseBody("Product with ID : " + productId + " deleted successfully !", null));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllProducts() {
        List<ProductResponseDto> result = productService.getAllProducts();
        return ResponseEntity
                .ok(new ApiResponseBody("All products retrieved successfully !", result));
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponseBody> getStoreById(@RequestParam String category) {
        List<ProductResponseDto> result = productService.getProductByCategory(category);
        return ResponseEntity
                .ok(new ApiResponseBody("Products with : " + category + " retrieved successfully !", result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchStoresByName(@RequestParam String productName) {
        List<ProductResponseDto> results = productService.searchProductsByName(productName);
        return ResponseEntity
                .ok(new ApiResponseBody("Products matching name retrieved successfully !", results));
    }

    @GetMapping("/price/range")
    public ResponseEntity<ApiResponseBody> filterProductByPriceRange(@RequestParam BigDecimal min , @RequestParam BigDecimal max) {
        List<ProductResponseDto> results = productService.filterProductsByPriceRange(min, max);
        return ResponseEntity
                .ok(new ApiResponseBody("Products within range : " + min + " and " + max +" retrieved successfully !", results));
    }


}
