package org.example.inventoryservice.inventoryitem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryItemWithRoomInfoDto extends InventoryItemDto {
    private String roomName;
    private Double area;
    private Integer floor;
    private Integer buildingId;
    private String buildingName;


    public InventoryItemWithRoomInfoDto(InventoryItemDto inventoryItemDto, RoomDto roomDto) {
        super(inventoryItemDto.getId(),
                inventoryItemDto.getInventoryId(),
                inventoryItemDto.getInventoryNumber(),
                roomDto.getId(),
                roomDto.getBuildingId(),
                inventoryItemDto.getInventoryName());
        this.roomName = roomDto.getName();
        this.area = roomDto.getArea();
        this.floor = roomDto.getFloor();
        this.buildingId = roomDto.getBuildingId();
        this.buildingName = roomDto.getBuildingName();


    }
}
