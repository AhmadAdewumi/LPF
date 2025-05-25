package com.ahmad.ProductFinder.service.imageService;

import com.ahmad.ProductFinder.dtos.response.CloudinaryResponseDto;
import com.ahmad.ProductFinder.dtos.response.ImageResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.CloudinaryException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Image;
import com.ahmad.ProductFinder.models.Product;
import com.ahmad.ProductFinder.repositories.ImageRepository;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.service.cloudinaryService.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public String uploadImage(Long productId,MultipartFile file, String folderName, String altText) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
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

        imageRepository.save(image);

        product.getImages().add(image);
        productRepository.save(product);

        return response.url();
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinaryService.deleteFile(publicId);
            imageRepository.deleteByPublicId(publicId);
        } catch (Exception e) {
            throw new CloudinaryException(
                    "Unable to delete image, please, Try again!", e
            );
        }
    }

    @Override
    public List<ImageResponseDto> getImagesByProductId(Long productId) {
        List<Image> results = imageRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to retrieve images for product with ID " + productId));
        return results.stream()
                .map(
                        image -> new ImageResponseDto(
                                image.getId(),
                                image.getUrl(),
                                image.getFileName(),
                                image.getAltText()
                        )
                ).toList();
    }

    @Override
    public ImageResponseDto getImageById(Long id) {
        Image image = imageRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException ("No image found with this 'id' " + id));
        return new ImageResponseDto(
                image.getId(),
                image.getUrl(),
                image.getFileName(),
                image.getAltText()
        );
    }


}
