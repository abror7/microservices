package org.example.inventoryservice.inventoryitem;

import org.example.inventoryservice.inventoryitem.dto.InventoryItemDto;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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
public class InventoryItemControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InventoryItemService inventoryItemService;

    Flux<InventoryItem> inventoryItems = Flux.just(
            InventoryItem.builder().id(1).inventoryId(1).inventoryNumber(12).build(),
            InventoryItem.builder().id(2).inventoryId(1).inventoryNumber(14).build());


    @Test
    public void getInventoryItemRoomInfoBySignNumberSuccessful() {
        String inventorySignNumber = "A23";
        int page = 0, size = 10;


        BDDMockito.given(inventoryItemService.getInventoryItemRoomInfoBySignNumber(inventorySignNumber, page, size))
                .willReturn(Flux.just());

        webTestClient.get().uri("/api/v1/inventories")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryItem.class)
                .hasSize(2);
    }

    @Test
    public void getAllInventoryItemsSuccessful() {
        int page = 0, size = 10;

        InventoryItem inventoryItem = InventoryItem.builder().build();

        BDDMockito.given(inventoryItemService.getAllInventoryItems(page, size))
                .willReturn(Flux.just(inventoryItem));

        webTestClient.get().uri("/api/v1/inventory-items")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryItem.class).hasSize(1)
                .contains(inventoryItem);
    }

    @Test
    public void getInventoryItemByIdSuccessful() {
        Integer id = 1;

        InventoryItemDto inventoryItem = InventoryItemDto.builder().build();

        BDDMockito.given(inventoryItemService.getInventoryItemById(id))
                .willReturn(Mono.just(inventoryItem));

        webTestClient.get().uri("/api/v1/inventory-items/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InventoryItemDto.class).isEqualTo(inventoryItem);
    }

    InventoryItem inventoryItem = InventoryItem.builder().id(1).inventoryId(1).inventoryNumber(11).build();

    @Test
    public void addInventoryItemSuccessful() {
        InventoryItemDto inventoryItemDto = InventoryItemDto.builder().build();

        BDDMockito.given(inventoryItemService.addInventoryItem(inventoryItemDto))
                .willReturn(Mono.just(inventoryItem));

        webTestClient.post().uri("/api/v1/inventory-items")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryItemDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Successfully saved!!")
                .jsonPath("$.data").isEqualTo(inventoryItem);
    }

    @Test
    public void updateInventoryItem_Successful() {
        int id = 1;
        InventoryItemDto inventoryItemDto = InventoryItemDto.builder().build();

        BDDMockito.given(inventoryItemService.updateInventoryItem(id, inventoryItemDto))
                .willReturn(Mono.just(inventoryItem));

        webTestClient.put().uri("/api/v1/inventory-items/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(inventoryItemDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Successfully updated!!")
                .jsonPath("$.data").isEqualTo(inventoryItem);
    }

    @Test
    public void deleteInventoryItemSuccessful() {
        int id = 1;
        BDDMockito.given(inventoryItemService.deleteInventoryItem(id))
                .willReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/inventory-items/{id}", id)
                .exchange()
                .expectStatus().isOk();
               }


}