package com.ahmad.ProductFinder.service.cloudinaryService;

import com.ahmad.ProductFinder.dtos.response.CloudinaryResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    CloudinaryResponseDto uploadFile(MultipartFile file,String folderName);
    void deleteFile(String publicId);
}
