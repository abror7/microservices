package org.example.buildingservice.building;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.buildingservice.room.RoomDto;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BuildingDto {
    private Integer id;
    private String name;
    private Double area;
    private List<RoomDto> rooms;

    public BuildingDto(Integer id, String name, Double area) {
        this.id = id;
        this.name = name;
        this.area = area;
    }
}
