package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.request.UpdateImageRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import com.ahmad.ProductFinder.service.imageService.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@Slf4j
@Tag(name = "Image Management", description = "APIs for uploading,updating,deleting,fetching images for a product")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(
            summary = "Upload an image for a product",
            description = "Uploads a single image(Multipart form data) for a product using the productId as a pointer to the product and you can also provide an altText",
            parameters = {
                    @Parameter(name = "productId", description = "The ID of the product to associate the image with", required = true, example = "1"),
                    @Parameter(name = "altText", description = "Alternative text for the image", example = "High quality image of a laptop")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image uploaded to cloudinary successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found for the given productId."),
                    @ApiResponse(responseCode = "500", description = "Internal server error, e.g., Cloudinary upload failure.")
            }
    )
    @PostMapping(value = "/upload/{productId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody> uploadImage(@PathVariable Long productId,
                                                       @RequestPart("file") MultipartFile file,
                                                       @RequestPart(value = "altText") String altText) {

        log.info("Uploading image for productId: {} with altText: {}", productId, altText);
        String imageUrl = imageService.uploadImage(productId, file, altText);
        log.info("Image uploaded successfully for productId: {}. Image URL: {}", productId, imageUrl);
        return ResponseEntity.ok(new ApiResponseBody("Image uploaded successfully for product " + productId, imageUrl));
    }



    @Operation(
            summary = "Update image details",
            description = "Updates the details (e.g., alt text) of an existing image using the image Id.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Image credentials to update image",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateImageRequestDto.class))
            ),
            parameters = {
                    @Parameter(name = "id", description = "The ID of the image to update", required = true, example = "101")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image details updated successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, e.g., invalid input in request body."),
                    @ApiResponse(responseCode = "404", description = "Image not found for the given ID."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @PatchMapping(value = "/update/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateImage(
            @PathVariable Long id,
            @RequestBody UpdateImageRequestDto requestDto) {
        log.info("Updating image details for imageId: {}", id);
        ImageResponseDto result = imageService.updateImageDetails(id, requestDto);
        log.info("Image details updated successfully for imageId: {}", id);
        return ResponseEntity.ok(new ApiResponseBody("Image details updated successfully", result));
    }

    @Operation(
            summary = "Get images by product ID",
            description = "Retrieves all images associated with a specific product.",
            parameters = {
                    @Parameter(name = "productId", description = "The ID of the product", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Images fetched successfully for the product.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Product not found or no images for the product."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponseBody> getImagesByProductId(@PathVariable Long productId) {
        log.info("Fetching images for productId: {}", productId);
        List<ImageResponseDto> results = imageService.getImagesByProductId(productId);
        log.info("Fetched {} image(s) for productId: {}", results.size(), productId);
        return ResponseEntity.ok(new ApiResponseBody("Images fetched successfully for product " + productId, results));
    }


    @Operation(
            summary = "Delete an image by public ID",
            description = "Deletes an image from Cloudinary and removes its record by its Cloudinary public ID.",
            parameters = {
                    @Parameter(name = "publicId", description = "The Cloudinary public ID of the image to delete", required = true, example = "product_images/laptop_xyz_123")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image deleted successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request, e.g., missing public ID."),
                    @ApiResponse(responseCode = "404", description = "Image not found for the given public ID in Cloudinary or your database."),
                    @ApiResponse(responseCode = "500", description = "Internal server error during deletion.")
            }
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseBody> deleteImage(@RequestParam String publicId) {
        log.info("Request to delete image with publicId: {}", publicId);
        imageService.deleteImage(publicId);
        log.info("Image with publicId: {} deleted successfully", publicId);
        return ResponseEntity.ok(new ApiResponseBody("Image with ID " + publicId + " deleted successfully!", null));
    }


    @Operation(
            summary = "Get image details by image ID (id from the db)",
            description = "Retrieves details of a single image by its image ID.",
            parameters = {
                    @Parameter(name = "id", description = "The internal database ID of the image", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image details fetched successfully.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseBody.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Image not found for the given ID."),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseBody> getImagesById(@PathVariable Long id) {
        log.info("Fetching image with id: {}", id);
        ImageResponseDto result = imageService.getImageById(id);
        log.info("Fetched image details for id: {}", id);
        return ResponseEntity.ok(new ApiResponseBody("Image with ID " + id + " fetched successfully!", result));
    }
}