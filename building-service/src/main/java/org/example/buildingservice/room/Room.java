package org.example.buildingservice.room;

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
public class Room {
    @Id
    private Integer id;
    private String name;
    private Double area;
    private Integer floor;
    private Integer buildingId;

    public Room(String name, Double area, Integer floor, Integer buildingId) {
        this.name = name;
        this.area = area;
        this.floor = floor;
        this.buildingId = buildingId;
    }
}
