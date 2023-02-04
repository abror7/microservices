package org.example.buildingservice.building;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.buildingservice.exception.CustomBadRequestException;
import org.example.buildingservice.exception.CustomGeneralException;
import org.example.buildingservice.exception.ResourceNotFoundException;
import org.example.buildingservice.exception.UniqueKeyException;
import org.example.buildingservice.room.Room;
import org.example.buildingservice.room.RoomRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;

    public Flux<BuildingDto> getAllBuildings(int page, int size) {
        if (page < 0 || size <= 0) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return buildingRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(this::convertToBuildingDto);
    }


    public Mono<BuildingDto> getBuildingById(Integer id) {
        return buildingRepository.findById(id)
                .map(this::convertToBuildingDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Building", id)));
    }


    @Transactional
    public Mono<BuildingDto> addBuilding(BuildingDto buildingDto) {
        Building building = convertFromDtoToBuilding(buildingDto);
        return buildingRepository.save(building)
                .onErrorMap(e -> {
                            if (e instanceof DuplicateKeyException)
                                return new UniqueKeyException("Building with the same name already exists.");
                            return new CustomGeneralException(e);

                        }
                )
                .flatMap(savedBuilding -> {
                    if (buildingDto.getRooms() != null && buildingDto.getRooms().size() != 0)
                        return saveRooms(buildingDto, savedBuilding);
                    return Mono.just(savedBuilding);
                })
                .map(this::convertToBuildingDto);
    }


    public Mono<BuildingDto> updateBuilding(Integer id, BuildingDto buildingDto) {
        return buildingRepository.findById(id)
                .flatMap(existingBuilding -> {
                    existingBuilding.setName(buildingDto.getName());
                    existingBuilding.setArea(buildingDto.getArea());
                    return buildingRepository.save(existingBuilding);
                })
                .map(this::convertToBuildingDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Building", id)));
    }

    public Mono<Boolean> deleteBuilding(Integer id) {
        return buildingRepository.deleteById(id)
                .onErrorMap(e -> new CustomBadRequestException("You can't delete an building with id: " + id))
                .flatMap(unused -> Mono.just(true));
    }

    private Mono<Building> saveRooms(BuildingDto buildingDto, Building savedBuilding) {
        List<Room> rooms = buildingDto.getRooms().stream()
                .map(roomDto ->
                        Room.builder()
                                .name(roomDto.getName())
                                .area(roomDto.getArea())
                                .floor(roomDto.getFloor())
                                .buildingId(savedBuilding.getId())
                                .build()
                ).collect(Collectors.toList());

        double totalRoomArea = rooms.stream()
                .mapToDouble(Room::getArea)
                .sum();

        if (totalRoomArea > savedBuilding.getArea()) {
            log.error("Bad request...");
            return Mono.error(new CustomBadRequestException("Building's area is not enough for the rooms"));
        }


        return roomRepository.saveAll(rooms)
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException) {
                        String errorMsg = "Room with the same name already exists.";
                        log.error(errorMsg);
                        throw new UniqueKeyException(errorMsg);
                    } else
                        throw new CustomGeneralException();
                })
                .then(Mono.just(savedBuilding));
    }


    private BuildingDto convertToBuildingDto(Building building) {
        return BuildingDto.builder()
                .id(building.getId())
                .name(building.getName())
                .area(building.getArea())
                .build();
    }

    private static Building convertFromDtoToBuilding(BuildingDto buildingDto) {
        return Building.builder()
                .name(buildingDto.getName())
                .area(buildingDto.getArea())
                .build();
    }

    public Mono<Boolean> checkBuildingIfExist(Integer id) {
        return buildingRepository.existsById(id)
                .flatMap(isExist -> {
                            if (!isExist)
                                return Mono.error(new ResourceNotFoundException("Building", id));
                            return Mono.just(true);
                        }
                );

    }
}
