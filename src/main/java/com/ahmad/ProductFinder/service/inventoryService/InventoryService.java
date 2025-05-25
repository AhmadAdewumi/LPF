package com.ahmad.ProductFinder.service.inventoryService;

import com.ahmad.ProductFinder.dtos.entityDto.StoreDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.models.Product;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.repositories.InventoryRepository;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.InventoryResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService implements IInventoryService {
    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, StoreRepository storeRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void createInventory(CreateInventoryRequestDto inventoryRequest) {
        Long storeId = inventoryRequest.getStoreId();
        Long productId = inventoryRequest.getProductId();

//        boolean productExists = productRepository.existsById(productId);
//        boolean storeExists = storeRepository.existsById(storeId);

        boolean inventoryExists = inventoryRepository.existsByStoreIdAndProductId(storeId, productId);
        if (inventoryExists) {
            throw new AlreadyExistsException("Duplicate entry detected , consider updating the resource");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with ID : " + storeId + " not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with ID : " + productId + " not found"));

        var inventory = buildInventory(inventoryRequest, store, product);
        inventoryRepository.save(inventory);
    }

    private Inventory buildInventory(CreateInventoryRequestDto dto, Store store, Product product) {
        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setStockQuantity(dto.getStockQuantity());
        inventory.setPrice(dto.getPrice());
        inventory.setIsActive(true);
        return inventory;
    }


    //SOFT DELETE
    @Override
    public void deleteInventoryById(Long inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID : " + inventoryId + "doesn't exist ! "));
        inventory.setIsActive(false);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateInventory(Long inventoryId, UpdateInventoryRequestDto dto) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID : " + inventoryId + " not found !"));

        if (dto.getPrice() == null || dto.getIsActive() == null || dto.getStockQuantity() == null) {
            throw new IllegalArgumentException("Ensure price, quantity and isActive are not null");
        }
        inventory.setPrice(dto.getPrice());
        inventory.setIsActive(dto.getIsActive());
        inventory.setStockQuantity(dto.getStockQuantity());
        inventory.setUpdatedAt(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public List<InventoryResponseDto> getAllInventories(InventoryResponseDto dto) {
        List<Inventory> activeOnes = inventoryRepository.findAllByIsActiveTrue();
        return activeOnes.stream()
                .map(inventory -> {
                    InventoryResponseDto responseDto = new InventoryResponseDto();
                    responseDto.setProductId(inventory.getProduct().getId());
                    responseDto.setProductName(inventory.getProduct().getName());
                    responseDto.setStoreId(inventory.getStore().getId());
                    responseDto.setStoreName(inventory.getStore().getName());
                    responseDto.setStoreAddress(inventory.getStore().getAddress().getCity());
                    responseDto.setStoreAddress(inventory.getStore().getAddress().getStreet());
                    responseDto.setStockQuantity(inventory.getStockQuantity());
                    responseDto.setPrice(inventory.getPrice());
                    responseDto.setIsActive(inventory.getIsActive());

                    return responseDto;
                }).toList();
    }

    @Override
    public List<InventoryResponseDto> getInventoryByStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Inventory Not Found , unable to retrieve inventory for store with ID : " + storeId);
        }
        List<Inventory> inventoryList = inventoryRepository.findInventoryByStoreIdAndIsActiveTrueOrderByPrice(storeId);
        return getInventoryResponseDtos(inventoryList);
    }

    @Override
    public List<InventoryResponseDto> getInventoryByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product Not Found , unable to retrieve for product with ID : " + productId);
        }
        List<Inventory> inventoryList = inventoryRepository.findInventoryByProductIdAndQuantityGreaterThan(productId);
        return getInventoryResponseDtos(inventoryList);
    }

    @Override
    public Integer getProductsStockLevel(Long storeId, Long productId) {
       boolean inventoryExists = inventoryRepository.existsByStoreIdAndProductId(storeId,productId);
       if (!inventoryExists){
           throw new ResourceNotFoundException("Inventory Not Found , unable to retrieve inventory for store with the store ID and product Id provided ");
       }
       return inventoryRepository.countStockForProductInStore(storeId,productId);
    }

    @Override
    public List<InventoryResponseDto> getInventoryForAProductWithinPriceRange(BigDecimal minimumPrice, BigDecimal maximumPrice) {
        if (minimumPrice == null || maximumPrice == null) {
            throw new IllegalArgumentException("Minimum and maximum price must not be null");
        }

        if (minimumPrice.compareTo(maximumPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        List<Inventory> inventories = inventoryRepository
                .findByPriceBetweenAndIsActiveTrueOrderByPriceAsc(minimumPrice, maximumPrice);

        return getInventoryResponseDtos(inventories);
    }


    private List<InventoryResponseDto> getInventoryResponseDtos(List<Inventory> inventories) {
        return inventories.stream()
                .map(inv -> {
                    InventoryResponseDto inventoryDto = new InventoryResponseDto();
                    inventoryDto.setCreatedAt(LocalDateTime.now());
                    inventoryDto.setPrice(inv.getPrice());
                    inventoryDto.setIsActive(inv.getIsActive());
                    inventoryDto.setStockQuantity(inv.getStockQuantity());

                    return inventoryDto;
                }).toList();
    }

    @Override
    public List<StoreDto> getStoreWhereSpecificProductAvailable(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product Not Found , unable to retrieve for product with ID : " + productId);
        }
        List<Inventory> inventories = inventoryRepository.findByProduct_IdAndIsActiveIsTrueAndStockQuantityGreaterThan(productId, 0);
        //                .distinct()
        return inventories.stream()
                .map(inventory -> {
                    StoreDto storeDto = new StoreDto();
                    storeDto.setStoreAddress(inventory.getStore().getAddress().getCity());
                    storeDto.setStoreAddress(inventory.getStore().getAddress().getStreet());
                    storeDto.setDescription(inventory.getStore().getDescription());

                    return storeDto;
                })
//                .distinct()
                .toList();
    }
}

