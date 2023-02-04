package org.example.buildingservice.building;

import org.example.buildingservice.payload.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class BuildingControllerIT {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BuildingService buildingService;

    Flux<BuildingDto> buildingDtos = Flux.just(
            BuildingDto.builder().id(1).name("Building 1").build(),
            BuildingDto.builder().id(2).name("Building 2").build());

    BuildingDto buildingDto = BuildingDto.builder().id(1).name("Building 1").build();


    @Test
    void getAllBuildings() {

        BDDMockito.given(buildingService.getAllBuildings(0, 10))
                .willReturn(buildingDtos);

        webTestClient.get().uri("/api/v1/buildings?page=1&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BuildingDto.class)
                .hasSize(2);
    }

    @Test
    void getBuildingById() {
        BDDMockito.given(buildingService.getBuildingById(1))
                .willReturn(Mono.just(buildingDto));

        webTestClient.get().uri("/api/v1/buildings/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BuildingDto.class);
    }

    @Test
    void checkBuildingIfExist() {
        Mono<Boolean> exist = Mono.just(true);
        BDDMockito.given(buildingService.checkBuildingIfExist(1))
                .willReturn(exist);

        webTestClient.get().uri("/api/v1/buildings/check-if-exist/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class);
    }

    @Test
    void addBuilding() {
        BDDMockito.given(buildingService.addBuilding(Mockito.any(BuildingDto.class)))
                .willReturn(Mono.just(buildingDto));

        webTestClient.post().uri("/api/v1/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildingDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Successfully saved!!");

    }

    @Test
    void updateBuilding() {
        BDDMockito.given(buildingService.updateBuilding(any(), Mockito.any(BuildingDto.class)))
                .willReturn(Mono.just(buildingDto));

        webTestClient.put().uri("/api/v1/buildings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildingDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertTrue(apiResponse.isSuccess());
                    assertEquals("Successfully updated!!", apiResponse.getMessage());
                    assertNotNull(apiResponse.getData());
                });
    }

    @Test
    void deleteBuilding() {
        BDDMockito.given(buildingService.deleteBuilding(1))
                .willReturn(Mono.just(true));

        webTestClient.delete().uri("/api/v1/buildings/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertTrue(apiResponse.isSuccess());
                    assertEquals("Successfully deleted!!", apiResponse.getMessage());
                    assertNotNull(apiResponse.getData());
                });
    }

}
