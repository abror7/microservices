package org.example.transactionservice.transactionhistory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransferInfoDto {
    private Integer fromBuildingId;
    private Integer toBuildingId;
    private List<TransferInventoryDto> inventories;
    private String description;

}
