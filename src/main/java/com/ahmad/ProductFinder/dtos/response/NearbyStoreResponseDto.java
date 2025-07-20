package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.models.Tag;

import java.util.Set;
import java.util.stream.Collectors;

public record NearbyStoreResponseDto(
        Long id,
        String name,
        AddressDto addressDto,
        String description,
        double latitude,
        double longitude,
        boolean isVerified,
        boolean isActive,
        Double distanceInMetres,
        Set<String> tags

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
                distanceInMetres,
                store.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
        );

    }
}
