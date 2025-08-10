package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.ProductDocs;
import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.ProductResponseDto;
import com.ahmad.ProductFinder.service.productService.IProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController implements ProductDocs {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = {"/add", "/create"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> createProduct(@RequestBody @Valid CreateProductRequestDto request) {
        log.info("POST api/v1/product/add - creating product: {}", request.name());
        ProductResponseDto result = productService.createProduct(request);
        log.info("Product created with ID: {}", result.id());
        return ResponseEntity.ok(new ApiResponseBody("Product Created Successfully", result));
    }

    @PatchMapping(value = "/update/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateProduct(@PathVariable Long productId,
                                                       @RequestBody @Valid UpdateProductRequestDto request) {
        log.info("PUT api/v1/product/update/productId - updating product: {}", request.name());
        ProductResponseDto updatedProduct = productService.updateProduct(productId, request);
        log.info("Product updated with ID: {}", updatedProduct.id());
        return ResponseEntity
                .ok(new ApiResponseBody(format("product with ID: %d , updated successfully!", productId), updatedProduct));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseBody> getProductById(@PathVariable Long productId) {
        log.info("GET api/v1/product/productId - retrieving product with ID: {}", productId);
        ProductResponseDto result = productService.getProductUsingProductId(productId);
        log.info("Product retrieved with ID: {}", result.id());
        return ResponseEntity
                .ok(new ApiResponseBody("Product with ID : " + productId + " retrieved successfully !", result));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponseBody> deleteProduct(@PathVariable Long productId) {
        log.info("GET api/v1/product/delete/productId - deleting product with ID: {}", productId);
        productService.deleteProductUsingProductId(productId);
        log.info("Product with ID: {}, deleted!", productId);
        return ResponseEntity
                .ok(new ApiResponseBody("Product with ID : " + productId + " deleted successfully !", null));
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllProducts() {
        log.info("GET api/v1/product/all, retrieving all products!");
        List<ProductResponseDto> result = productService.getAllProducts();
        log.info("All Products retrieved successfully!");
        return ResponseEntity
                .ok(new ApiResponseBody("All products retrieved successfully !", result));
    }


    @GetMapping("/category")
    public ResponseEntity<ApiResponseBody> getProductByCategory(@RequestParam String category) {
        log.info("GET api/v1/product/category , getting product by category!");
        List<ProductResponseDto> result = productService.getProductByCategory(category);
        log.info("Products with category , {} , retrieved successfully!", category);
        return ResponseEntity
                .ok(new ApiResponseBody("Products with category : " + category + " retrieved successfully !", result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchProductsByName(@RequestParam String productName) {
        log.info("Received request to search products by name: {}", productName);
        List<ProductResponseDto> results = productService.searchProductsByProductName(productName);
        log.info("Found {} product(s) matching name: {}", results.size(), productName);
        return ResponseEntity
                .ok(new ApiResponseBody("Products matching name retrieved successfully !", results));
    }

    @GetMapping("/price/range")
    public ResponseEntity<ApiResponseBody> filterProductByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        log.info("Received request to filter products in price range: {} - {}", min, max);

        List<ProductResponseDto> results = productService.filterProductsByPriceRange(min, max);

        log.info("Found {} product(s) in price range {} - {}", results.size(), min, max);

        return ResponseEntity
                .ok(new ApiResponseBody("Products within range : " + min + " and " + max + " retrieved successfully !", results));
    }


}
