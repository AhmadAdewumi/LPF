package com.ahmad.ProductFinder.service.imageService;

import com.ahmad.ProductFinder.dtos.request.UpdateImageRequestDto;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import com.ahmad.ProductFinder.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    String uploadImage(Long productId , MultipartFile file,String altText);

    void deleteImage(String publicId);
    List<ImageResponseDto> getImagesByProductId(Long productId);

    ImageResponseDto updateImageDetails(Long imageId, UpdateImageRequestDto request);

    ImageResponseDto getImageById(Long id);

}
