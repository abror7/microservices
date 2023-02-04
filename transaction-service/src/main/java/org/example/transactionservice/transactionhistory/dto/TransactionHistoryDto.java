package org.example.transactionservice.transactionhistory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.transactionservice.transactionitem.TransactionItemDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionHistoryDto {
    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer fromBuildingId;
    private String fromBuildingName;
    private Integer toBuildingId;
    private String toBuildingName;
    private String description;
}
