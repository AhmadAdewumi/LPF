package com.ahmad.ProductFinder.service.inventoryService;

import com.ahmad.ProductFinder.dtos.entityDto.StoreDto;
import com.ahmad.ProductFinder.dtos.request.CreateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.InventoryResponseDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.models.Product;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.repositories.InventoryRepository;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@Service
@Slf4j
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
    public Inventory createInventory(CreateInventoryRequestDto inventoryRequest) {
        log.info("createInventory() invoked | storeId={}, productId={}", inventoryRequest.getStoreId(), inventoryRequest.getProductId());
        Long storeId = inventoryRequest.getStoreId();
        Long productId = inventoryRequest.getProductId();

//        boolean productExists = productRepository.existsById(productId);
//        boolean storeExists = storeRepository.existsById(storeId);

        boolean inventoryExists = inventoryRepository.existsByStoreIdAndProductId(storeId, productId);
        if (inventoryExists) {
            log.warn("Inventory already exists | storeId={}, productId={}", storeId, productId);
            throw new AlreadyExistsException("Duplicate entry detected , consider updating the resource");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store not found | storeId={}", storeId);
                    return new ResourceNotFoundException("Store with ID : " + storeId + " not found");
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found | productId={}", productId);
                    return new ResourceNotFoundException("Product with ID : " + productId + " not found");
                });

        var inventory = buildInventory(inventoryRequest, store, product);
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory created successfully | inventoryId={}", saved.getId());
        return saved;
    }

    private Inventory buildInventory(CreateInventoryRequestDto dto, Store store, Product product) {
        log.debug("buildInventory() helper invoked | storeId={}, productId={}", store.getId(), product.getId());
        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setStockQuantity(dto.getStockQuantity());
        inventory.setPrice(dto.getPrice());
        inventory.setIsActive(true);
        inventory.setCreatedAt(LocalDateTime.now());
        return inventory;
    }

    //SOFT DELETE IMPL maybe later
    @Override
    public void deleteInventoryByInventoryId(Long inventoryId) {
        log.info("deleteInventoryById() invoked | inventoryId={}", inventoryId);
        inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> {
                    log.warn("Inventory not found | inventoryId={}", inventoryId);
                    return new ResourceNotFoundException(format("Inventory with ID:,%d , not found !", inventoryId));
                });
        inventoryRepository.deleteById(inventoryId);
        log.info("Inventory deleted | inventoryId={}", inventoryId);
    }

    @Override
    public Inventory updateInventoryByInventoryId(Long inventoryId, UpdateInventoryRequestDto dto) {
        log.info("updateInventory() invoked | inventoryId={}, price={}, quantity={}, isActive={}",
                inventoryId, dto.getPrice(), dto.getStockQuantity(), dto.getIsActive());

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> {
                    log.error("Inventory not found | inventoryId={}", inventoryId);
                    return new ResourceNotFoundException("Inventory with ID : " + inventoryId + " not found !");
                });

        if (dto.getPrice() == null || dto.getIsActive() == null || dto.getStockQuantity() == null) {
            log.warn("Invalid update payload: null values | inventoryId={}", inventoryId);
            throw new IllegalArgumentException("Price, quantity, and isActive must all be provided for inventory update.");
        }

        inventory.setPrice(dto.getPrice());
        inventory.setIsActive(dto.getIsActive());
        inventory.setStockQuantity(dto.getStockQuantity());
        inventory.setUpdatedAt(LocalDateTime.now());
        Inventory updated = inventoryRepository.save(inventory);
        log.info("Inventory updated successfully | inventoryId={}", inventoryId);
        return updated;
    }

    @Override
    public List<InventoryResponseDto> getAllActiveInventories() {
        log.info("getAllInventories() invoked");
        List<Inventory> activeOnes = inventoryRepository.findAllByIsActiveTrue();
        log.debug("Active inventories retrieved: {}", activeOnes.size());
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
    public List<InventoryResponseDto> getInventoryUsingStoreId(Long storeId) {
        log.info("getInventoryByStore() invoked | storeId={}", storeId);
        if (!storeRepository.existsById(storeId)) {
            log.warn("Store not found | storeId={}", storeId);
            throw new ResourceNotFoundException("Inventory Not Found , unable to retrieve inventory for store with ID : " + storeId);
        }
        List<Inventory> inventoryList = inventoryRepository.findInventoryByStoreIdAndIsActiveTrueOrderByPrice(storeId);
        log.debug("Inventory entries found for storeId={}: {}", storeId, inventoryList.size());
        return getInventoryResponseDtos(inventoryList);
    }

    @Override
    public List<InventoryResponseDto> getInventoryUsingProductId(Long productId) {
        log.info("getInventoryByProduct() invoked | productId={}", productId);
        if (!productRepository.existsById(productId)) {
            log.warn("Product not found | productId={}", productId);
            throw new ResourceNotFoundException("Product Not Found , unable to retrieve for product with ID : " + productId);
        }
        List<Inventory> inventoryList = inventoryRepository.findInventoryByProductIdAndQuantityGreaterThan(productId);
        log.debug("Inventory entries found for productId={}: {}", productId, inventoryList.size());
        return getInventoryResponseDtos(inventoryList);
    }

    @Override
    public Integer getProductsStockLevel(Long storeId, Long productId) {
        log.info("getProductsStockLevel() invoked | storeId={}, productId={}", storeId, productId);
        boolean inventoryExists = inventoryRepository.existsByStoreIdAndProductId(storeId, productId);
        if (!inventoryExists) {
            log.warn("Inventory not found for stock level check | storeId={}, productId={}", storeId, productId);
            throw new ResourceNotFoundException(format("Inventory Not Found , unable to retrieve inventory for store with store ID: %d and product Id: %d ,provided ",storeId,productId));
        }
        Integer stockLevel = inventoryRepository.countStockForProductInStore(storeId, productId);
        log.info("Stock level retrieved | storeId={}, productId={}, stockLevel={}", storeId, productId, stockLevel);
        return stockLevel;
    }

    @Override
    public List<InventoryResponseDto> getInventoryForProductWithinPriceRange(BigDecimal minimumPrice, BigDecimal maximumPrice) {
        log.info("getInventoryForAProductWithinPriceRange() invoked | minPrice={}, maxPrice={}", minimumPrice, maximumPrice);
        if (minimumPrice == null || maximumPrice == null) {
            log.warn("Null price range passed to inventory filter");
            throw new IllegalArgumentException("Minimum and maximum price must not be null");
        }

        if (minimumPrice.compareTo(maximumPrice) > 0) {
            log.warn("Invalid price range | minPrice={} > maxPrice={}", minimumPrice, maximumPrice);
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        List<Inventory> inventories = inventoryRepository
                .findByPriceBetweenAndIsActiveTrueOrderByPriceAsc(minimumPrice, maximumPrice);
        log.debug("Inventory entries in price range: {}", inventories.size());

        return getInventoryResponseDtos(inventories);
    }

    private List<InventoryResponseDto> getInventoryResponseDtos(List<Inventory> inventories) {
        log.info("getInventoryResponseDtos() helper invoked | count={}", inventories.size());
        return inventories.stream()
                .map(inv -> {
                    InventoryResponseDto inventoryDto = new InventoryResponseDto();
                    inventoryDto.setProductId(inv.getProduct().getId());
                    inventoryDto.setStoreId(inv.getStore().getId());
                    inventoryDto.setProductName(inv.getProduct().getName());
                    inventoryDto.setStoreName(inv.getStore().getName());
                    inventoryDto.setPrice(inv.getPrice());
                    inventoryDto.setIsActive(inv.getIsActive());
                    inventoryDto.setStockQuantity(inv.getStockQuantity());

                    return inventoryDto;
                }).toList();
    }

    @Override
    public List<StoreDto> getStoresWithProductInStock(Long productId) {
        log.info("getStoreWhereSpecificProductAvailable() invoked | productId={}", productId);
        if (!productRepository.existsById(productId)) {
            log.warn("Product not found | productId={}", productId);
            throw new ResourceNotFoundException("Product Not Found , unable to retrieve for product with ID : " + productId);
        }
        List<Inventory> inventories = inventoryRepository.findByProduct_IdAndIsActiveIsTrueAndStockQuantityGreaterThan(productId, 0);
        log.debug("Stores found with available productId={}: {}", productId, inventories.size());

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
