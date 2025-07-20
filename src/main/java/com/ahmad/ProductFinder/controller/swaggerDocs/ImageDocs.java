package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.request.UpdateImageRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Images", description = "Operations for uploading, retrieving, and managing product images")
public interface ImageDocs {

    @Operation(
            summary = "Upload product image",
            description = "Uploads an image for a product. Accepts multipart/form-data with an optional alt text.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID to associate with the image", required = true, example = "1"),
                    @Parameter(name = "altText", description = "Optional alt text for accessibility", example = "Back view of blue sneakers")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image uploaded successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found."),
                    @ApiResponse(responseCode = "500", description = "Upload failed.")
            }
    )
    ResponseEntity<ApiResponseBody> uploadImageToCloudinary(@PathVariable Long productId,
                                                            @org.springframework.web.bind.annotation.RequestPart("file") MultipartFile file,
                                                            @org.springframework.web.bind.annotation.RequestPart("altText") String altText);

    @Operation(
            summary = "Update image details",
            description = "Updates alt text or other metadata for a specific image.",
            parameters = {
                    @Parameter(name = "imageId", description = "Image ID from the database", required = true, example = "101")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New alt text or other editable fields",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UpdateImageRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image updated successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input."),
                    @ApiResponse(responseCode = "404", description = "Image not found."),
                    @ApiResponse(responseCode = "500", description = "Update failed.")
            }
    )
    ResponseEntity<ApiResponseBody> updateImageDetails(@PathVariable Long imageId, @RequestBody UpdateImageRequestDto requestDto);

    @Hidden
    @Operation(
            summary = "Get images for a product",
            description = "Fetches all uploaded images for a given product ID.",
            parameters = {
                    @Parameter(name = "productId", description = "Product ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Images retrieved.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found or no images."),
                    @ApiResponse(responseCode = "500", description = "Error occurred.")
            }
    )
    ResponseEntity<ApiResponseBody> getImagesByProductId(@PathVariable Long productId);

    @Operation(
            summary = "Delete image by Cloudinary public ID",
            description = "Removes the image from Cloudinary and deletes its record using its public ID.",
            parameters = {
                    @Parameter(name = "publicId", description = "Cloudinary public ID", required = true, example = "product_images/laptop_xyz_123")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image deleted.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Missing or invalid public ID."),
                    @ApiResponse(responseCode = "404", description = "Image not found."),
                    @ApiResponse(responseCode = "500", description = "Deletion failed.")
            }
    )
    ResponseEntity<ApiResponseBody> deleteImageByPublicId(@RequestParam String publicId);

    @Operation(
            summary = "Get image by image ID",
            description = "Fetches metadata for a specific image using its database ID.",
            parameters = {
                    @Parameter(name = "imageId", description = "Internal database ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image retrieved.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiResponseBody.class))),
                    @ApiResponse(responseCode = "404", description = "Image not found."),
                    @ApiResponse(responseCode = "500", description = "Error occurred.")
            }
    )
    ResponseEntity<ApiResponseBody> getImagesByImageId(@PathVariable Long imageId);
}
