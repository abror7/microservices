package org.example.inventoryservice.inventoryitem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InventoryItemDto {
    private Integer id; //res
    private Integer inventoryId; //res
    private Integer inventoryNumber; //req & res
    private Integer roomId;// req & res
    private Integer buildingId;// req & res
    private String inventoryName;
}
