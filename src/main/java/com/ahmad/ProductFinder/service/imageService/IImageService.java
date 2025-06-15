package com.ahmad.ProductFinder.service.imageService;

import com.ahmad.ProductFinder.dtos.request.UpdateImageRequestDto;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    String uploadImageToCloudinary(Long productId , MultipartFile file, String altText);

    void deleteImageUsingPublicId(String publicId);
    List<ImageResponseDto> getImagesByProductId(Long productId);

    ImageResponseDto updateImageDetails(Long imageId, UpdateImageRequestDto request);

    ImageResponseDto getImageById(Long id);

}
