package org.example.inventoryservice.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InventoryDto {
    private Integer id;
    private String name;
    private String description;
    private String inventorySign;
    private List<InventoryItemDto> inventoryItems;
    private Integer quantity;
    private Integer roomId;
    private Integer buildingId;
    private Integer startNumberOfInventoryItem;
    private Integer endNumberOfInventoryItem;
}
