package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import com.ahmad.ProductFinder.models.Store;

import java.time.LocalDateTime;

public record StoreResponseDto(
        Long id,
        String name,
        AddressDto addressDto,
        String description,
        double latitude,
        double longitude,
        boolean isVerified,
        boolean isActive,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)

{
    public static StoreResponseDto from(Store store) {
        return new StoreResponseDto(
                store.getId(),
                store.getName(),
                AddressDto.from(store.getAddress()),
                store.getDescription(),
                store.getLocation().getY(),
                store.getLocation().getX(),
                store.isVerified(),
                store.isActive(),
                store.getOwner().getId(),
                store.getCreatedAt(),
                store.getUpdatedAt()

        );
    }
}