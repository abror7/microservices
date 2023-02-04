package org.example.inventoryservice.inventoryitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table
public class InventoryItem {
    @Id
    private Integer id;

    private Integer inventoryId;

    private Integer inventoryNumber;

    private Integer roomId;
    private Integer buildingId;

    public InventoryItem(Integer inventoryId, Integer inventoryNumber, Integer buildingId) {
        this.inventoryId = inventoryId;
        this.inventoryNumber = inventoryNumber;
        this.roomId = buildingId;
    }
}
