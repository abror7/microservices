package org.example.transactionservice.transactionitem;

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
public class TransactionItem {
    @Id
    private Integer id;
    private Integer transactionHistoryId;
    private Integer inventoryId;
    private Integer quantity;
}
