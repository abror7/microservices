package org.example.buildingservice.building;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table
public class Building {
    @Id
    private Integer id;
    @Column
    private String name;
    @Column
    private Double area;

    public Building(String name, Double area) {
        this.name = name;
        this.area = area;
    }
}
