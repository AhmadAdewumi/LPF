package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.projection.StoreProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StoreMapper {
    public List<NearbyStoreResponseDto> toNearbyStoreDtos(List<StoreProjection> projections){
        return projections
                .stream()
//                .map(proj-> mapToDto(proj))
                .map(this::mapToDto)
                .toList();
    }


    private NearbyStoreResponseDto mapToDto(StoreProjection storeProjection){
        return new NearbyStoreResponseDto(
                storeProjection.getId(),
                storeProjection.getName(),
                new AddressDto(storeProjection.getStreet(), storeProjection.getCity(), storeProjection.getState(), storeProjection.getCountry(), storeProjection.getPostal_code()),
                storeProjection.getDescription(),
                storeProjection.getLatitude(),
                storeProjection.getLongitude(),
                true,
                true,
                storeProjection.getDistance_in_metres()
        );
    }
}
