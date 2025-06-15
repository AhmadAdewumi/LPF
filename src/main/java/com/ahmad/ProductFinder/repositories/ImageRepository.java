package com.ahmad.ProductFinder.repositories;

import com.ahmad.ProductFinder.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findByProductId(Long productId);

    void deleteByPublicId(String publicId);

    Optional<Image> findByPublicId(String publicId);
}
