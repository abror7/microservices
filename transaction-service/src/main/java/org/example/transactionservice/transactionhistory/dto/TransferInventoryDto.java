package org.example.transactionservice.transactionhistory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransferInventoryDto {
    private Integer inventoryId;
    private Integer quantity;
}
