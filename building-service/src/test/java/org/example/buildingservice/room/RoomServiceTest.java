package org.example.buildingservice.room;

import org.example.buildingservice.building.Building;
import org.example.buildingservice.building.BuildingRepository;
import org.example.buildingservice.exception.CustomBadRequestException;
import org.example.buildingservice.exception.ResourceNotFoundException;
import org.example.buildingservice.exception.UniqueKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BuildingRepository buildingRepository;

    @InjectMocks
    private RoomService roomService;

    private final Integer ROOM_ID = 1;
    private final String ROOM_NAME = "Room 1";
    private final Double ROOM_AREA = 100.0;
    private final Integer ROOM_FLOOR = 1;
    private final Integer BUILDING_ID = 1;

    private RoomDto roomDto;
    private Room room;

    @BeforeEach
    public void setUp() {
        roomDto = RoomDto.builder()
                .id(ROOM_ID)
                .name(ROOM_NAME)
                .area(ROOM_AREA)
                .floor(ROOM_FLOOR)
                .buildingId(BUILDING_ID)
                .build();

        room = Room.builder()
                .id(ROOM_ID)
                .name(ROOM_NAME)
                .area(ROOM_AREA)
                .floor(ROOM_FLOOR)
                .buildingId(BUILDING_ID)
                .build();
    }

    @Test
    void testGetAllRooms() {
        when(roomRepository.findAll())
                .thenReturn(Flux.just(room));

        Flux<Room> result = roomService.getAllRooms(0, 1);

        StepVerifier.create(result)
                .expectNext(room)
                .verifyComplete();
    }

    @Test
    void testGetAllRoomsAndInvalidPageSizeAndShouldThrowException() {
        Flux<Room> result = roomService.getAllRooms(-1, 0);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void testGetRoomById() {
        when(roomRepository.getRoomById(ROOM_ID))
                .thenReturn(Mono.just(roomDto));

        Mono<RoomDto> result = roomService.getRoomById(ROOM_ID);

        StepVerifier.create(result)
                .expectNextMatches(roomDto -> roomDto.getId().equals(ROOM_ID)
                        && roomDto.getName().equals(ROOM_NAME)
                        && roomDto.getArea().equals(ROOM_AREA)
                        && roomDto.getFloor().equals(ROOM_FLOOR)
                        && roomDto.getBuildingId().equals(BUILDING_ID))
                .verifyComplete();
    }

    @Test
    void testGetRoomByIdAndShouldThrowResourceNotFoundException() {
        when(roomRepository.getRoomById(ROOM_ID)).thenReturn(Mono.empty());
        Mono<RoomDto> result = roomService.getRoomById(ROOM_ID);
        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void testAddRoom() {

        Building building = Building.builder()
                .id(1)
                .name("Test Building")
                .area(1000.0)
                .build();

        Mono<Building> buildingMono = Mono.just(building);
        when(buildingRepository.findById(1)).thenReturn(buildingMono);
        when(roomRepository.getAreaAllRoomsByBuildingId(1)).thenReturn(Mono.just(70.0));
        when(roomRepository.save(any(Room.class))).thenReturn(Mono.just(Room.builder().id(1).build()));

        StepVerifier.create(roomService.addRoom(roomDto))
                .expectNextMatches(result -> result.getId() == 1)
                .verifyComplete();
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    public void testAddRoomAndBuildingNotFound() {
        // Given
        RoomDto roomDto = new RoomDto();
        roomDto.setBuildingId(0);
        roomDto.setArea(100.0);

        // When
        when(buildingRepository.findById(anyInt())).thenReturn(Mono.empty());
        StepVerifier.create(roomService.addRoom(roomDto))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    public void testAddRoomAndNotEnoughArea() {
        // Given
        Integer buildingId = 1;
        Building building = new Building();
        building.setId(buildingId);
        building.setArea(1000.0);

        RoomDto roomDto = new RoomDto();
        roomDto.setBuildingId(buildingId);
        roomDto.setArea(9999.0);

        // When
        when(buildingRepository.findById(buildingId)).thenReturn(Mono.just(building));
        when(roomRepository.getAreaAllRoomsByBuildingId(buildingId)).thenReturn(Mono.just(999.0));

        StepVerifier.create(roomService.addRoom(roomDto))
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    public void testAddRoomAndDuplicateName() {
        // Given
        RoomDto roomDto = new RoomDto();
        roomDto.setBuildingId(1);
        roomDto.setArea(100.0);
        Integer buildingId = 1;
        Building building = new Building();
        building.setId(buildingId);
        building.setArea(1000.0);

        // When
        when(buildingRepository.findById(buildingId)).thenReturn(Mono.just(building));
        when(roomRepository.getAreaAllRoomsByBuildingId(roomDto.getBuildingId())).thenReturn(Mono.just(0.0));
        when(roomRepository.save(any(Room.class))).thenReturn(Mono.error(new DuplicateKeyException(any())));

        StepVerifier.create(roomService.addRoom(roomDto))
                .expectError(UniqueKeyException.class)
                .verify();
    }


    @Test
    void testUpdateRoom() {
        Mono<Room> roomMono = Mono.just(room);
        when(roomRepository.findById(1)).thenReturn(roomMono);
        when(roomRepository.save(any(Room.class))).thenReturn(Mono.just(room));

        StepVerifier.create(roomService.updateRoom(1, roomDto))
                .expectNextMatches(result -> result.getId() == 1)
                .verifyComplete();
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void testDeleteRoom() {
        when(roomRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(roomService.deleteRoom(1))
                .verifyComplete();
        verify(roomRepository).deleteById(1);
    }


}
               
