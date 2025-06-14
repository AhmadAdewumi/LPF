package com.ahmad.ProductFinder.service.inventoryService;

import com.ahmad.ProductFinder.dtos.entityDto.StoreDto;
import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.InventoryResponseDto;
import com.ahmad.ProductFinder.models.Inventory;

import java.math.BigDecimal;
import java.util.List;

public interface IInventoryService {
    Inventory createInventory(CreateInventoryRequestDto inventoryRequest);
    void deleteInventoryByInventoryId(Long inventoryId);
    Inventory updateInventoryByInventoryId(Long inventoryId , UpdateInventoryRequestDto inventoryRequest);
    List<InventoryResponseDto> getAllActiveInventories();                //for admin
    List<InventoryResponseDto> getInventoryUsingStoreId(Long storeId);
    List<InventoryResponseDto> getInventoryUsingProductId(Long productId);
    Integer getProductsStockLevel(Long storeId , Long productId);

    List<InventoryResponseDto> getInventoryForProductWithinPriceRange(BigDecimal minimumPrice, BigDecimal maximumPrice);

    List<StoreDto> getStoresWithProductInStock(Long productId);
}
