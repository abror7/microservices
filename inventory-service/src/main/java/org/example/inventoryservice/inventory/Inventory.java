package org.example.inventoryservice.inventory;

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
public class Inventory {
    @Id
    private Integer id;
    private String name;
    private String description;
    private String inventorySign;


}
