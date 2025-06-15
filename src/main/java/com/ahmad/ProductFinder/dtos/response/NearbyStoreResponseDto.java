package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import com.ahmad.ProductFinder.models.Store;

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

) {
    public static NearbyStoreResponseDto from(Store store,Double distanceInMetres){
        return new NearbyStoreResponseDto(
                store.getId(),
                store.getName(),
                new AddressDto(store.getAddress().getStreet(),
                        store.getAddress().getCity(),
                        store.getAddress().getState(),
                        store.getAddress().getCountry(),
                        store.getAddress().getPostalCode()),
                store.getDescription(),
                store.getLatitude(),
                store.getLongitude(),
                true,
                true,
                distanceInMetres
        );

    }
}
