package org.example.buildingservice.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.buildingservice.building.BuildingRepository;
import org.example.buildingservice.exception.CustomBadRequestException;
import org.example.buildingservice.exception.CustomGeneralException;
import org.example.buildingservice.exception.ResourceNotFoundException;
import org.example.buildingservice.exception.UniqueKeyException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;

    public Flux<Room> getAllRooms(int page, int size) {
        if (page < 0 || size <= 0){
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return roomRepository.findAll()
                .skip((long) page * size)
                .take(size);
    }

    public Mono<RoomDto> getRoomById(Integer id) {
        return roomRepository.getRoomById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Room", id)));
    }

    public Mono<RoomDto> addRoom(RoomDto roomDto) {
        return buildingRepository.findById(roomDto.getBuildingId())
                .switchIfEmpty(Mono.error(() -> new ResourceNotFoundException("Building", roomDto.getBuildingId())))
                .flatMap(building -> roomRepository.getAreaAllRoomsByBuildingId(building.getId())
                        .defaultIfEmpty(0.0)
                        .map(totalAreaOfRooms -> {
                            if (totalAreaOfRooms + roomDto.getArea() > building.getArea())
                                throw new CustomBadRequestException("Building's area is not enough for the rooms");
                            return building;
                        })
                        .flatMap(building1 -> {
                            Room room = convertFromDtoToRoom(roomDto);
                            return roomRepository.save(room)
                                    .onErrorResume(e -> {
                                        if (e instanceof DuplicateKeyException) {
                                            String errorMsg = "Room with the same name already exists.";
                                            log.error(errorMsg);
                                            throw new UniqueKeyException(errorMsg);
                                        } else {
                                            log.error(e.getMessage());
                                            throw new CustomGeneralException();
                                        }
                                    })
                                    .map(this::convertToRoomDto);
                        })
                );


    }


    public Mono<RoomDto> updateRoom(Integer id, RoomDto roomDto) {
        return roomRepository.findById(id)
                .flatMap(existingRoom -> {
                    existingRoom.setName(roomDto.getName());
                    existingRoom.setArea(roomDto.getArea());
                    existingRoom.setFloor(roomDto.getFloor());
                    existingRoom.setBuildingId(roomDto.getBuildingId());
                    return roomRepository.save(existingRoom);
                })
                .map(this::convertToRoomDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Room", id)));
    }

    public Mono<Void> deleteRoom(Integer id) {
        return roomRepository.deleteById(id);
    }

    private RoomDto convertToRoomDto(Room room) {
        return RoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .area(room.getArea())
                .floor(room.getFloor())
                .buildingId(room.getBuildingId())
                .build();
    }

    private static Room convertFromDtoToRoom(RoomDto roomDto) {
        return Room.builder()
                .name(roomDto.getName())
                .area(roomDto.getArea())
                .floor(roomDto.getFloor())
                .buildingId(roomDto.getBuildingId())
                .build();
    }
}
