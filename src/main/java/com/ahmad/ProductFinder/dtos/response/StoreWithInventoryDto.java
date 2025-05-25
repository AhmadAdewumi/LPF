package com.ahmad.ProductFinder.dtos.response;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import com.ahmad.ProductFinder.models.Store;

import java.util.List;

public record StoreWithInventoryDto(
        Long id,
        String name,
        AddressDto address,
        String description,
        double latitude,
        double longitude,
        List<ProductInventoryDto> products
) {

    public static StoreWithInventoryDto from(Store store) {
        return new StoreWithInventoryDto(
                store.getId(),
                store.getName(),
                AddressDto.from(store.getAddress()),
                store.getDescription(),
                store.getLocation().getY(),
                store.getLocation().getX(),
                store.getInventory().stream()
                        .filter(inv -> inv.getIsActive() && inv.getStockQuantity() > 0)
                        .map(ProductInventoryDto::from)
                        .toList()
        );
    }
}