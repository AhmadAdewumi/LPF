package com.ahmad.ProductFinder.service.cloudinaryService;

import com.ahmad.ProductFinder.dtos.response.CloudinaryResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    CloudinaryResponseDto uploadFileToCloudinary(MultipartFile file, String folderName);
    void deleteFileUsingPublicId(String publicId);

    String getOptimizedUrl(String publicId);
}
