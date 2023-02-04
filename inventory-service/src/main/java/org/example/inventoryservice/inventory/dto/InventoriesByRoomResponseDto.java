package org.example.inventoryservice.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InventoriesByRoomResponseDto {
    private Integer id;
    private String name;
    private String inventorySign;
    private Integer quantity;

}
