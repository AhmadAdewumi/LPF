package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.request.CreateProductRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateProductRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@SecurityRequirement(name = "bearerAuth")
@Hidden
public interface ProductDocs {

    @Operation(
            summary = "Create product",
            description = "Registers a new product with name, description, category, and price.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateProductRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid product data."),
                    @ApiResponse(responseCode = "500", description = "Server error during creation.")
            }
    )
    ResponseEntity<ApiResponseBody> createProduct(@RequestBody @Valid CreateProductRequestDto request);

    @Operation(
            summary = "Update product",
            description = "Updates a product by its ID.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "1")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateProductRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid update data."),
                    @ApiResponse(responseCode = "404", description = "Product not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> updateProduct(@PathVariable Long productId,
                                                  @RequestBody @Valid UpdateProductRequestDto request);

    @Operation(
            summary = "Get product by ID",
            description = "Fetches a product by its ID.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getProductById(@PathVariable Long productId);

    @Operation(
            summary = "Delete product",
            description = "Deletes a product by its ID.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> deleteProduct(@PathVariable Long productId);

    @Operation(
            summary = "Get all products",
            description = "Fetches all products in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All products retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getAllProducts();

    @Operation(
            summary = "Get products by category",
            description = "Fetches products by category name.",
            parameters = {
                    @Parameter(name = "category", description = "Category name", required = true, example = "Electronics")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products retrieved.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "No products in category."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> getProductByCategory(@RequestParam String category);

    @Operation(
            summary = "Search products by name",
            description = "Performs a keyword search on product names.",
            parameters = {
                    @Parameter(name = "productName", description = "Search keyword", required = true, example = "monitor")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Matching products returned.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> searchProductsByName(@RequestParam String productName);

    @Operation(
            summary = "Filter products by price range",
            description = "Fetches products within the given price range.",
            parameters = {
                    @Parameter(name = "min", description = "Minimum price", required = true, example = "100.00"),
                    @Parameter(name = "max", description = "Maximum price", required = true, example = "500.00")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products within range returned.",
                            content = @Content(schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid price range."),
                    @ApiResponse(responseCode = "500", description = "Server error.")
            }
    )
    ResponseEntity<ApiResponseBody> filterProductByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max);
}
