package com.ahmad.ProductFinder.service.cloudinaryService;

import com.ahmad.ProductFinder.dtos.response.CloudinaryResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.CloudinaryException;
import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService implements ICloudinaryService{
    @Resource
    private Cloudinary cloudinary;

    @Override
    public CloudinaryResponseDto uploadFile(MultipartFile file, String folderName) {
        try {
            Map<String,Object> options = new HashMap<>();
            options.put("fileName",folderName);

            Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),options);

            String url = (String)uploadResult.get("secure_url");
            String publicId = (String)uploadResult.get("public_id");
            String format = (String) uploadResult.get("format");
            Number bytes = (Number) uploadResult.get("bytes");
            Long size = (bytes != null) ? bytes.longValue() : null;

            return new CloudinaryResponseDto(url,publicId,format,size);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, new HashMap<>());
            String resultStatus = (String) result.get("result");

            if (!"ok".equalsIgnoreCase(resultStatus)) {
                throw new CloudinaryException("Failed to delete file from Cloudinary: " + resultStatus, null);
            }
        } catch (IOException | RuntimeException e) {
            throw new CloudinaryException("Error deleting file from Cloudinary", e);
        }
    }
}
