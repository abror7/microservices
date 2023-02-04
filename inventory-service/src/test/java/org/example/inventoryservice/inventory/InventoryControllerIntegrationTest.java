package org.example.inventoryservice.inventory;

import org.example.inventoryservice.inventory.dto.InventoriesByRoomResponseDto;
import org.example.inventoryservice.inventory.dto.InventoryDto;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
class InventoryControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InventoryService inventoryService;

    Flux<Inventory> inventories = Flux.just(
            Inventory.builder().id(1).inventorySign("A").name("Test name").build(),
            Inventory.builder().id(2).inventorySign("B").name("Test name 2").build());

    Mono<Inventory> inventory = Mono.just(Inventory.builder().id(1).name("Test name").inventorySign("A").build());
    InventoryDto inventoryDto = InventoryDto.builder().id(1).name("Test name").inventorySign("A").build();

    @Test
    void getAllInventories() {

        BDDMockito.given(inventoryService.getAllInventories(0, 10)).willReturn(inventories);

        webTestClient.get().uri("/api/v1/inventories")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Inventory.class)
                .hasSize(2);
    }

    @Test
    void getInventoriesByRoomId() {
        Flux<InventoriesByRoomResponseDto> res = Flux.just(
                new InventoriesByRoomResponseDto(1, "Test name", "A", 4),
                new InventoriesByRoomResponseDto(2, "Test name 2", "A", 5)
        );
        BDDMockito.given(inventoryService.getInventoriesByRoomId(1, 0, 10)).willReturn(res);

        webTestClient.get().uri("/api/v1/inventories/by-room-id/{roomId}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoriesByRoomResponseDto.class)
                .hasSize(2);
    }

    @Test
    void getInventoryById() {
        BDDMockito.given(inventoryService.getInventoryById(1)).willReturn(inventory);

        webTestClient.get().uri("/api/v1/inventories/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Inventory.class);
    }

    @Test
    void addInventory() {
        BDDMockito.given(inventoryService.addInventory(Mockito.any(InventoryDto.class)))
                .willReturn(inventory);

        webTestClient.post().uri("/api/v1/inventories")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Successfully saved!!");
    }

    @Test
    void updateInventory() {
        BDDMockito.given(inventoryService.updateInventory(Mockito.anyInt(), Mockito.any(InventoryDto.class)))
                .willReturn(Mono.just(inventoryDto));

        webTestClient.put().uri("/api/v1/inventories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Successfully updated!!");
    }

    @Test
    void deleteInventory() {
        BDDMockito.given(inventoryService.deleteInventory(Mockito.anyInt()))
                .willReturn(Mono.just(true));

        webTestClient.delete().uri("/api/v1/inventories/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Successfully deleted!!");
    }


}
