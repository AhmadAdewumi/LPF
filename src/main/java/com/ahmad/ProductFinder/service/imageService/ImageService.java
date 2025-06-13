package com.ahmad.ProductFinder.service.imageService;

import com.ahmad.ProductFinder.dtos.request.UpdateImageRequestDto;
import com.ahmad.ProductFinder.dtos.response.CloudinaryResponseDto;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.CloudinaryException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Image;
import com.ahmad.ProductFinder.models.Product;
import com.ahmad.ProductFinder.repositories.ImageRepository;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.service.cloudinaryService.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;

    public ImageService(ImageRepository imageRepository, CloudinaryService cloudinaryService, ProductRepository productRepository) {
        this.imageRepository = imageRepository;
        this.cloudinaryService = cloudinaryService;
        this.productRepository = productRepository;
    }

    @Override
    public String uploadImage(Long productId,MultipartFile file, String altText) {
        log.info("Uploading image for Product ID: {}, FileName: {}", productId, file.getOriginalFilename());
        String folderName="product-finder";
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found for ID: {}", productId);
                    return new ResourceNotFoundException("Product not found with id: " + productId);
                });
        CloudinaryResponseDto response = cloudinaryService.uploadFile(file, folderName);
        Image image = Image.builder()
                .url(response.url())
                .fileName(file.getOriginalFilename())
                .altText(altText)
                .publicId(response.publicId())
                .format(file.getContentType())
                .size(file.getSize())
                .product(product)
                .build();
        product.addImage(image);

        imageRepository.save(image);
        productRepository.save(product);
        log.info("Image uploaded and linked to product. Public ID: {}, URL: {}", response.publicId(), response.url());

        return response.url();
    }

    @Transactional
    @Override
    public void deleteImage(String publicId) {
        log.info("Deleting image from Cloudinary and database. Public ID: {}", publicId);
        Image image = imageRepository.findByPublicId(publicId).orElseThrow(()-> new ResourceNotFoundException("Image not found!"));
        cloudinaryService.deleteFile(publicId);
        Product product = image.getProduct();
        product.removeImage(image);
        try {
            imageRepository.deleteByPublicId(publicId);
            productRepository.save(product);
            log.info("Image deleted from database. Public ID: {}", publicId);
        } catch (Exception e) {
            log.error("Failed to delete image from DB. Public ID: {}, Error: {}", publicId, e.getMessage(), e);
            throw new CloudinaryException(
                    "Unable to delete image, please, Try again!", e
            );
        }
    }

    @Override
    public List<ImageResponseDto> getImagesByProductId(Long productId) {
        log.debug("Fetching images for product ID: {}", productId);
        var product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found while fetching images. ID: {}", productId);
                    return new ResourceNotFoundException("No product found with ID: " + productId);
                });
        List<Image> results = imageRepository.findByProductId(productId);
        if (results.isEmpty()){
            log.warn("No images found for product ID: {}", productId);
            throw new ResourceNotFoundException("Unable to retrieve images for product with ID " + productId);
        }
        log.info("Found {} images for product ID: {}", results.size(), productId);
        return results.stream()
                .map(
                        image -> new ImageResponseDto(
                                image.getId(),
                                cloudinaryService.getOptimizedUrl(image.getPublicId()),
                                image.getFileName(),
                                image.getAltText()
                        )
                ).toList();
    }

    @Override
    public ImageResponseDto updateImageDetails(Long imageId, UpdateImageRequestDto request) {
        log.info("Updating image metadata. Image ID: {}", imageId);
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.warn("Image not found for update. ID: {}", imageId);
                    return new ResourceNotFoundException(format("Image with ID: %d not found!",imageId));
                });

        if (request.altText() != null)
            image.setAltText(request.altText());

        if (request.fileName() != null)
            image.setFileName(request.fileName());

        if (request.productId() != null) {
            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> {
                        log.warn("Product not found when updating image. Product ID: {}", request.productId());
                        return new ResourceNotFoundException("Product not found");
                    });
            image.setProduct(product);
        }

        imageRepository.save(image);
        log.info("Image metadata updated successfully. Image ID: {}", imageId);

        return new ImageResponseDto(
                image.getId(),
                image.getUrl(),
                image.getFileName(),
                image.getAltText()
        );
    }

    @Override
    public ImageResponseDto getImageById(Long id) {
        log.debug("Fetching image by ID: {}", id);
        Image image = imageRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn("Image not found. ID: {}", id);
                    return new ResourceNotFoundException ("No image found with this 'id' " + id);
                });
        log.info("Image retrieved successfully. ID: {}", id);
        return new ImageResponseDto(
                image.getId(),
                image.getUrl(),
                image.getFileName(),
                image.getAltText()
        );
    }
}
