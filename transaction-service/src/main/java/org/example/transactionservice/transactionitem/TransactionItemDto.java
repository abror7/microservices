package org.example.transactionservice.transactionitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionItemDto {
    private Integer id;
    private Integer inventoryId;
    private String inventoryName;
    private String inventorySign;
    private Integer quantity;
    private Integer transactionHistoryId;
}
