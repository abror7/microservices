package org.example.buildingservice.building;

import org.example.buildingservice.exception.CustomBadRequestException;
import org.example.buildingservice.exception.ResourceNotFoundException;
import org.example.buildingservice.exception.UniqueKeyException;
import org.example.buildingservice.room.RoomDto;
import org.example.buildingservice.room.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BuildingServiceTest {

    @InjectMocks
    private BuildingService buildingService;

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private RoomRepository roomRepository;


    private final BuildingDto buildingDto = BuildingDto.builder().id(1).name("Building 1").area(1000.0).build();
    private final Building building = Building.builder().id(1).name("Building 1").area(1000.0).build();


    @Test
    void testGetAllBuildings() {
        // Arrange
        Building building1 = new Building(1, "Building 1", 20.5);
        Building building2 = new Building(2, "Building 2", 50.0);
        List<Building> buildingList = Arrays.asList(building1, building2);
        Flux<Building> buildingFlux = Flux.fromIterable(buildingList);
        when(buildingRepository.findAll()).thenReturn(buildingFlux);

        // Act
        Flux<BuildingDto> result = buildingService.getAllBuildings(0, 2);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(b -> b.getId() == 1 && b.getName().equals("Building 1") && b.getArea() == 20.5)
                .expectNextMatches(b -> b.getId() == 2 && b.getName().equals("Building 2") && b.getArea() == 50.0)
                .verifyComplete();
    }

    @Test
    public void testGetAllBuildingsInvalidPageShouldReturnError() {
        Flux<BuildingDto> result = buildingService.getAllBuildings(-1, 2);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    public void testGetAllBuildingsAndInvalidSizeAndShouldReturnError() {
        Flux<BuildingDto> result = buildingService.getAllBuildings(0, 0);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void testGetAllBuildingsBadRequest() {
        // Act
        Flux<BuildingDto> result = buildingService.getAllBuildings(-1, 0);

        // Assert
        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    public void testGetBuildingByIdSuccess() {
        Integer id = 1;

        when(buildingRepository.findById(id)).thenReturn(Mono.just(building));

        Mono<BuildingDto> result = buildingService.getBuildingById(id);

        StepVerifier.create(result)
                .expectNext(buildingDto)
                .expectComplete()
                .verify();
        verify(buildingRepository, times(1)).findById(id);
    }

    @Test
    public void testGetBuildingByIdFailure() {
        Integer id = 2;
        when(buildingRepository.findById(id)).thenReturn(Mono.empty());

        Mono<BuildingDto> result = buildingService.getBuildingById(id);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
        verify(buildingRepository, times(1)).findById(id);
    }

    @Test
    void addBuildingAndShouldReturnBuildingDto() {

        buildingDto.setRooms(
                List.of(
                        RoomDto.builder()
                                .name("Room 1")
                                .buildingId(1)
                                .floor(2)
                                .area(200.0)
                                .build()
                )
        );
        when(buildingRepository.save(any(Building.class))).thenReturn(Mono.just(building));
        when(roomRepository.saveAll(anyList())).thenReturn(Flux.empty());

        Mono<BuildingDto> actualBuilding = buildingService.addBuilding(buildingDto);

        StepVerifier.create(actualBuilding)
                .assertNext(b -> assertEquals(buildingDto.getName(), b.getName()))
                .expectComplete()
                .verify();
        verify(buildingRepository).save(any(Building.class));
        verify(roomRepository).saveAll(anyList());
    }

    @Test
    public void addBuildingAndShouldReturnCustomBadRequestExceptionWhenTotalRoomAreaIsGreaterThanBuildingArea() {
        buildingDto.setRooms((Arrays.asList(
                RoomDto.builder().name("Room 1").area(5000.0).floor(1).buildingId(1).build(),
                RoomDto.builder().name("Room 2").area(6000.0).floor(2).buildingId(1).build()
        )));
        when(buildingRepository.save(any(Building.class))).thenReturn(Mono.just(building));

        StepVerifier.create(buildingService.addBuilding(buildingDto))
                .expectError(CustomBadRequestException.class)
                .verify();

        verify(roomRepository, never()).saveAll(anyList());
    }
    @Test
    void addBuildingAndShouldThrowUniqueKeyExceptionAndWhenBuildingWithTheSameNameAlreadyExists() {
        when(buildingRepository.save(any(Building.class)))
                .thenReturn(Mono.error(new DuplicateKeyException("Building with the same name already exists.")));

        Mono<BuildingDto> actualBuilding = buildingService.addBuilding(buildingDto);

        StepVerifier.create(actualBuilding)
                .expectError(UniqueKeyException.class)
                .verify();
        verify(buildingRepository).save(any(Building.class));
    }


    @Test
    void updateBuildingAndShouldReturnBuildingDto() {
        when(buildingRepository.findById(1)).thenReturn(Mono.just(building));
        when(buildingRepository.save(any(Building.class))).thenReturn(Mono.just(building));

        Mono<BuildingDto> actualBuilding = buildingService.updateBuilding(1, buildingDto);

        StepVerifier.create(actualBuilding)
                .assertNext(b -> assertEquals(buildingDto.getName(), b.getName()))
                .expectComplete()
                .verify();
        verify(buildingRepository).findById(1);
        verify(buildingRepository).save(any(Building.class));
    }

    @Test
    public void updateBuildingAndShouldThrowResourceNotFoundException() {
        Integer id = 1;

        when(buildingRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(buildingService.updateBuilding(id, buildingDto))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    public void testDeleteBuilding() {
        // Given
        Integer id = 1;
        Mono<Void> voidMono = Mono.empty();
        when(buildingRepository.deleteById(id)).thenReturn(voidMono);

        // When
        Mono<Boolean> result = buildingService.deleteBuilding(id);

        // Then
        verify(buildingRepository).deleteById(id);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }


}