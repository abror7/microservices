package org.example.buildingservice.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoomDto {
    private Integer id;
    private String name;
    private Double area;
    private Integer floor;
    private Integer buildingId;
    private String buildingName;

}
