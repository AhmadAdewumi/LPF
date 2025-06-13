package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.ProductResponseDto;
import com.ahmad.ProductFinder.service.productService.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/v1/product")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Create a new product",
            description = "Registers a new product in the system with its name, description, category and price.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details for creation",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateProductRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", // Consider 201 Created for resource creation
                            description = "Product created successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid product data provided (e.g., missing name, invalid price)."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during product creation."
                    )
            }
    )
    @PostMapping(value = {"/add","/create"},consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> createProduct(@RequestBody @Valid CreateProductRequestDto request) {
        log.info("POST api/v1/product/add - creating product: {}", request.name());
        ProductResponseDto result = productService.createProduct(request);
        log.info("Product created with ID: {}", result.id());
        return ResponseEntity.ok(new ApiResponseBody("Product Created Successfully", result));
    }


    @Operation(
            summary = "Update an existing product",
            description = "Updates the details of an existing product by using the ID of the product.",
            parameters = {
                    @Parameter(name = "productId", description = "The unique ID of the product to update", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateProductRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid update data provided or validation errors."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Product with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during product update."
                    )
            }
    )

    @PatchMapping(value = "/update/{productId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateStore(@PathVariable Long productId,
                                                       @RequestBody @Valid UpdateProductRequestDto request) {
        log.info("PUT api/v1/product/update/productId - updating product: {}", request.name());
        ProductResponseDto updatedProduct = productService.updateProduct(productId, request);
        log.info("Product updated with ID: {}", updatedProduct.id());
        return ResponseEntity
                .ok(new ApiResponseBody(format("product with ID: %d , updated successfully!", productId), updatedProduct));
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves the details of a single product by its unique ID.",
            parameters = {
                    @Parameter(name = "productId", description = "The unique ID of the product to retrieve", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Product with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during product retrieval."
                    )
            }
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseBody> getProductById(@PathVariable Long productId) {
        log.info("GET api/v1/product/productId - retrieving product with ID: {}", productId);
        ProductResponseDto result = productService.getProductById(productId);
        log.info("Product retrieved with ID: {}", result.id());
        return ResponseEntity
                .ok(new ApiResponseBody("Product with ID : " + productId + " retrieved successfully !", result));
    }

    @Operation(
            summary = "Delete a product by ID",
            description = "Deletes a product from the system based on its unique ID. This action is irreversible.",
            parameters = {
                    @Parameter(name = "productId", description = "The unique ID of the product to delete", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product deleted successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Product with the given ID does not exist."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during product deletion."
                    )
            }
    )
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponseBody> deleteStore(@PathVariable Long productId) {
        log.info("GET api/v1/product/delete/productId - deleting product with ID: {}", productId);
        productService.deleteProduct(productId);
        log.info("Product with ID: {}, deleted!", productId);
        return ResponseEntity
                .ok(new ApiResponseBody("Product with ID : " + productId + " deleted successfully !", null));
    }


    @Operation(
            summary = "Get all products",
            description = "Retrieves a list of all products currently available in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All products retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> getAllProducts() {
        log.info("GET api/v1/product/all, retrieving all products!");
        List<ProductResponseDto> result = productService.getAllProducts();
        log.info("All Products retrieved successfully!");
        return ResponseEntity
                .ok(new ApiResponseBody("All products retrieved successfully !", result));
    }

    @Operation(
            summary = "Get products by category",
            description = "Retrieves a list of products belonging to a specific category.",
            parameters = {
                    @Parameter(name = "category", description = "The category name to filter products by", required = true, example = "Electronics")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products for the specified category retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - No products found for the given category."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during retrieval."
                    )
            }
    )
    @GetMapping("/category")
    public ResponseEntity<ApiResponseBody> getProductByCategory(@RequestParam String category) {
        log.info("GET api/v1/product/category , getting product by category!");
        List<ProductResponseDto> result = productService.getProductByCategory(category);
        log.info("Products with category , {} , retrieved successfully!", category);
        return ResponseEntity
                .ok(new ApiResponseBody("Products with category : " + category + " retrieved successfully !", result));
    }

    @Operation(
            summary = "Search products by name",
            description = "Searches for products whose names contain the provided keyword.",
            parameters = {
                    @Parameter(name = "productName", description = "The keyword to search for in product names", required = true, example = "monitor")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products matching the name retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during search."
                    )
            }
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponseBody> searchProductsByName(@RequestParam String productName) {
        log.info("Received request to search products by name: {}", productName);
        List<ProductResponseDto> results = productService.searchProductsByName(productName);
        log.info("Found {} product(s) matching name: {}", results.size(), productName);
        return ResponseEntity
                .ok(new ApiResponseBody("Products matching name retrieved successfully !", results));
    }

    @Operation(
            summary = "Filter products by price range",
            description = "Retrieves products whose prices fall within a specified minimum and maximum range.",
            parameters = {
                    @Parameter(name = "min", description = "The minimum price for filtering", required = true, example = "100.00"),
                    @Parameter(name = "max", description = "The maximum price for filtering", required = true, example = "500.00")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products within the specified price range retrieved successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid price range provided."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An unexpected error occurred during filtering."
                    )
            }
    )
    @GetMapping("/price/range")
    public ResponseEntity<ApiResponseBody> filterProductByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        log.info("Received request to filter products in price range: {} - {}", min, max);

        List<ProductResponseDto> results = productService.filterProductsByPriceRange(min, max);

        log.info("Found {} product(s) in price range {} - {}", results.size(), min, max);

        return ResponseEntity
                .ok(new ApiResponseBody("Products within range : " + min + " and " + max + " retrieved successfully !", results));
    }


}
