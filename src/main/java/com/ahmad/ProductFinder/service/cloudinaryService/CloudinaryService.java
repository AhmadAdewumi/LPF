package com.ahmad.ProductFinder.service.cloudinaryService;

import com.ahmad.ProductFinder.dtos.response.CloudinaryResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.CloudinaryException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService implements ICloudinaryService {
    @Resource
    private Cloudinary cloudinary;

    @Override
    public CloudinaryResponseDto uploadFileToCloudinary(MultipartFile file, String folderName) {
        log.info("Uploading file to Cloudinary. Filename: {}, Folder: {}", file.getOriginalFilename(), folderName);

        if (file.isEmpty()) {
            log.warn("Upload failed: file is empty");
            throw new IllegalArgumentException("Cannot upload an empty file!");
        }
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("use_filename", true);
            options.put("unique_filename", false);


            //            Map<String,Object> uploadParams = new HashMap<>();
            //            uploadParams.put("file",inputStream);
            //            uploadParams.putAll(options);

            //max file size should be 4 mb
            if (file.getSize() > 4 * 1024 * 1024) {
                log.warn("Upload failed: File size {} exceeds 4MB", file.getSize());
                throw new CloudinaryException("File too large, max is 4MB");
            }

            log.debug("Uploading to Cloudinary with options: {}", options);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            log.debug("Upload result from Cloudinary: {}", uploadResult);

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            String format = (String) uploadResult.get("format");
            Number bytes = (Number) uploadResult.get("bytes");
            Long size = (bytes != null) ? bytes.longValue() : null;

            log.info("File uploaded successfully to Cloudinary. URL: {}, Public ID: {}", url, publicId);

            return new CloudinaryResponseDto(url, publicId, format, size);

        } catch (IOException e) {
            log.error("IOException occurred while uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read file for upload: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during Cloudinary upload: {}", e.getMessage(), e);
            throw new RuntimeException("Cloudinary upload failed! :" + e.getMessage());
        }
    }

    @Override
    public void deleteFileUsingPublicId(String publicId) {
        log.info("Attempting to delete file from Cloudinary. Public ID: {}", publicId);
        try {
            //            Map<String, String> param = new HashMap<>();
            //            param.put("folder", "Product-Images");
            //            param.put("invalidate", "true");

            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
            String resultStatus = (String) result.get("result");

            if (!"ok".equalsIgnoreCase(resultStatus)) {
                log.warn("Cloudinary delete failed. Status: {}", resultStatus);
                throw new CloudinaryException("Failed to delete file from Cloudinary: " + resultStatus);
            }

            log.info("File deleted successfully from Cloudinary. Public ID: {}", publicId);
        } catch (IOException | RuntimeException e) {
            log.error("Error deleting file from Cloudinary. Public ID: {}. Message: {}", publicId, e.getMessage(), e);
            throw new CloudinaryException("Error deleting file from Cloudinary", e);
        }
    }

    @Override
    public String getOptimizedUrl(String publicId) {
        log.debug("Generating optimized URL for Public ID: {}", publicId);
        String optimizedUrl =
                cloudinary.url()
                        .transformation(
                                new Transformation<>().quality("auto:low")
                                        .fetchFormat("auto").dpr("auto").crop("scale"))
                        .generate(publicId);
        log.info("Optimized URL generated: {}", optimizedUrl);
        return optimizedUrl;
    }
}
