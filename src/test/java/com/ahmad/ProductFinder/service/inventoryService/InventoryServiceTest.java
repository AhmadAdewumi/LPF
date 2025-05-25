package com.ahmad.ProductFinder.service.inventoryService;

import com.ahmad.ProductFinder.models.Inventory;
import com.ahmad.ProductFinder.repositories.InventoryRepository;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import com.ahmad.ProductFinder.dtos.request.UpdateInventoryRequestDto;
import com.ahmad.ProductFinder.dtos.response.InventoryResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    public void returnInventoryListIfShopIsActive(){
        //Arrange
        Long storeId = 1L;
        Inventory inv = new Inventory();
        inv.setPrice(BigDecimal.valueOf(98.9));
        inv.setIsActive(true);
        inv.setStockQuantity(10);

        List<Inventory> inventoryList = List.of(inv);
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(inventoryRepository.findInventoryByStoreIdAndIsActiveTrueOrderByPrice(storeId)).thenReturn(inventoryList);

        //Act
        List<InventoryResponseDto> result = inventoryService.getInventoryByStore(storeId);

        //Assert
        assertThat(result).hasSize(1);
        assertThat(result)
                .hasSize(1)
                .extracting("price","isActive","stockQuantity")
                .containsExactly(tuple(BigDecimal.valueOf(98.9),true,10));
    }

    @Test
    public void returnInventoryListUsingProductId(){
        //Arrange
        Long productId = 1L;
        Inventory inv = new Inventory();
        inv.setPrice(BigDecimal.valueOf(98.9));
        inv.setIsActive(true);
        inv.setStockQuantity(10);

        List<Inventory> inventoryList = List.of(inv);
        when(productRepository.existsById(productId)).thenReturn(true);
        when(inventoryRepository.findInventoryByProductIdAndQuantityGreaterThan(  productId)).thenReturn(inventoryList);

        //Act
        List<InventoryResponseDto> result = inventoryService.getInventoryByProduct(productId);

        //Assert
        assertThat(result).hasSize(1);
        assertThat(result)
                .hasSize(1)
                .extracting("price","isActive","stockQuantity")
                .containsExactly(tuple(BigDecimal.valueOf(98.9),true,10));
    }


    @Test
    public void shouldUpdateInventorySuccessfully(){
        //Arrange
        Long inventoryId = 1L;
        Inventory existingInventory = new Inventory();
        existingInventory.setIsActive(false);
        existingInventory.setPrice(BigDecimal.valueOf(20));
        existingInventory.setStockQuantity(5);

        UpdateInventoryRequestDto dto = new UpdateInventoryRequestDto();
        dto.setIsActive(true);
        dto.setPrice(BigDecimal.valueOf(50));
        dto.setStockQuantity(10);

        when(inventoryRepository.findById(inventoryId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(action -> action.getArgument(0));

        //act
        Inventory result = inventoryService.updateInventory(inventoryId, dto);

        //Assert
        assertThat(result.getPrice()).isEqualByComparingTo("50");
        assertThat(result.getStockQuantity()).isEqualTo(10);
        assertThat(result.getIsActive()).isTrue();
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
