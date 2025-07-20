package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.ImageDocs;
import com.ahmad.ProductFinder.dtos.request.UpdateImageRequestDto;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import com.ahmad.ProductFinder.service.imageService.ImageService;
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
public class ImageController implements ImageDocs {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    @PostMapping(value = "/upload/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseBody> uploadImageToCloudinary(@PathVariable Long productId,
                                                                   @RequestPart("file") MultipartFile file,
                                                                   @RequestPart(value = "altText") String altText) {

        log.info("Uploading image for productId: {} with altText: {}", productId, altText);
        String imageUrl = imageService.uploadImageToCloudinary(productId, file, altText);
        log.info("Image uploaded successfully for productId: {}. Image URL: {}", productId, imageUrl);
        return ResponseEntity.ok(new ApiResponseBody("Image uploaded successfully for product " + productId, imageUrl));
    }

    @Override
    @PatchMapping(value = "/update/{imageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody> updateImageDetails(@PathVariable Long imageId, @RequestBody UpdateImageRequestDto requestDto) {
        log.info("Updating image details for imageId: {}", imageId);
        ImageResponseDto result = imageService.updateImageDetails(imageId, requestDto);
        log.info("Image details updated successfully for imageId: {}", imageId);
        return ResponseEntity.ok(new ApiResponseBody("Image details updated successfully", result));
    }

    @Override
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponseBody> getImagesByProductId(@PathVariable Long productId) {
        log.info("Fetching images for productId: {}", productId);
        List<ImageResponseDto> resultList = imageService.getImagesByProductId(productId);
        log.info("Fetched {} image(s) for productId: {}", resultList.size(), productId);
        return ResponseEntity.ok(new ApiResponseBody("Images fetched successfully for product " + productId, resultList));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseBody> deleteImageByPublicId(@RequestParam String publicId) {
        log.info("Request to delete image with publicId: {}", publicId);
        imageService.deleteImageUsingPublicId(publicId);
        log.info("Image with publicId: {} deleted successfully", publicId);
        return ResponseEntity.ok(new ApiResponseBody("Image with ID " + publicId + " deleted successfully!", null));
    }

    @Override
    @GetMapping("/{imageId}")
    public ResponseEntity<ApiResponseBody> getImagesByImageId(@PathVariable Long imageId) {
        log.info("Fetching image with id: {}", imageId);
        ImageResponseDto result = imageService.getImageById(imageId);
        log.info("Fetched image details for id: {}", imageId);
        return ResponseEntity.ok(new ApiResponseBody("Image with ID " + imageId + " fetched successfully!", result));
    }
}