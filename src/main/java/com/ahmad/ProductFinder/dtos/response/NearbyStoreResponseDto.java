package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;

public record NearbyStoreResponseDto(
        Long id,
        String name,
        AddressDto addressDto,
        String description,
        double latitude,
        double longitude,
        boolean isVerified,
        boolean isActive,
        Double distanceInMetres

) {}
