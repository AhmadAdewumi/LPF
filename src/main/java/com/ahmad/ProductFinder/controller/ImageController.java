package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import com.ahmad.ProductFinder.service.imageService.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")

public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @PostMapping("/upload/{productId}")
    public ResponseEntity<ApiResponseBody> uploadImage(@PathVariable Long productId,
                                                       @RequestPart("file") MultipartFile file,
                                                       @RequestPart("folder") String folderName,
                                                       @RequestPart(value = "altText") String altText) {
        String imageUrl = imageService.uploadImage(productId, file, folderName, altText);
        return ResponseEntity.ok(new ApiResponseBody("Image uploaded successfully for product " + productId, imageUrl));
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponseBody> getImagesByProductId(@PathVariable Long productId) {
        List<ImageResponseDto> results = imageService.getImagesByProductId(productId);
        return ResponseEntity.ok(new ApiResponseBody("Images fetched successfully for product " + productId, results));
    }


    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<ApiResponseBody> deleteImage(@PathVariable String publicId) {
        imageService.deleteImage(publicId);
        return ResponseEntity.ok(new ApiResponseBody("Image with ID " + publicId + " deleted successfully!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseBody> getImagesById(@PathVariable Long id){
        ImageResponseDto result = imageService.getImageById(id);
        return ResponseEntity.ok(new ApiResponseBody("Image with ID " + id + "fetched successfully!" ,result));
    }
}