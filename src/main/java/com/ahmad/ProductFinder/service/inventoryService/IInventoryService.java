package com.ahmad.ProductFinder.service.inventoryService;

import com.ahmad.ProductFinder.dtos.entityDto.StoreDto;
import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.InventoryResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IInventoryService {
    Inventory createInventory(CreateInventoryRequestDto inventoryRequest);
    void deleteInventoryById(Long inventoryId);
    Inventory updateInventory(Long inventoryId , UpdateInventoryRequestDto inventoryRequest);
    List<InventoryResponseDto> getAllInventories();                //for admin
    List<InventoryResponseDto> getInventoryByStore(Long storeId);
    List<InventoryResponseDto> getInventoryByProduct(Long productId);
    Integer getProductsStockLevel(Long storeId , Long productId);

    List<InventoryResponseDto> getInventoryForAProductWithinPriceRange(BigDecimal minimumPrice, BigDecimal maximumPrice);

    List<StoreDto> getStoreWhereSpecificProductAvailable(Long productId);
}
