package com.ahmad.ProductFinder.dtos.entityDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDto {
    private String storeName;
    private String storeAddress;
    private String description;

}
