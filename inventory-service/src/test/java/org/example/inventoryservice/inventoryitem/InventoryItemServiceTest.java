package org.example.inventoryservice.inventoryitem;

import okhttp3.mockwebserver.MockWebServer;
import org.example.inventoryservice.exception.CustomBadRequestException;
import org.example.inventoryservice.exception.ResourceNotFoundException;
import org.example.inventoryservice.exception.UniqueKeyException;
import org.example.inventoryservice.inventory.InventoryRepository;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemDto;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemWithRoomInfoDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {

    @InjectMocks
    private InventoryItemService inventoryItemService;
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private WebClient.Builder webClientBuilder;

    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }


    @BeforeEach
    void setUp() {
        inventoryItemService = new InventoryItemService(inventoryItemRepository, inventoryRepository, webClientBuilder);
    }

    InventoryItemDto inventoryItemDto = new InventoryItemDto(1, 1, 23, 1, 1, "Test name");

    @Test
    void getAllInventoryItemsSuccess() {
        BDDMockito.given(inventoryItemRepository.findAll())
                .willReturn(Flux.just(
                        InventoryItem.builder().build(),
                        InventoryItem.builder().build()
                ));

        Flux<InventoryItem> allInventoryItems = inventoryItemService.getAllInventoryItems(0, 2);

        StepVerifier.create(allInventoryItems)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAllInventoryItemsBadRequest() {
        Flux<InventoryItem> allInventoryItems = inventoryItemService.getAllInventoryItems(-1, 0);

        StepVerifier.create(allInventoryItems)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void getInventoryItemByIdSuccess() {
        BDDMockito.given(inventoryItemRepository.getInventoryItemById(1))
                .willReturn(Mono.just(inventoryItemDto));

        Mono<InventoryItemDto> inventoryItemDtoMono = inventoryItemService.getInventoryItemById(1);

        StepVerifier.create(inventoryItemDtoMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getInventoryItemByIdNotFound() {
        BDDMockito.given(inventoryItemRepository.getInventoryItemById(1))
                .willReturn(Mono.empty());

        Mono<InventoryItemDto> inventoryItemDtoMono = inventoryItemService.getInventoryItemById(1);

        StepVerifier.create(inventoryItemDtoMono)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void addInventoryItemSuccess() {
        BDDMockito.given(inventoryItemRepository.save(any(InventoryItem.class)))
                .willReturn(Mono.just(InventoryItem.builder().build()));

        Mono<InventoryItem> addInventoryItem = inventoryItemService.addInventoryItem(inventoryItemDto);

        StepVerifier.create(addInventoryItem)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void addInventoryItemAlreadyExist() {
        InventoryItemDto inventoryItemDto = InventoryItemDto.builder().inventoryId(1).inventoryNumber(1).buildingId(1).build();
        InventoryItem inventoryItem = new InventoryItem(1, 1, 1);
        BDDMockito.given(inventoryItemRepository.save(any(InventoryItem.class)))
                .willReturn(Mono.error(new UniqueKeyException("InventoryItem with the same name already exists.")));
        Mono<InventoryItem> result = inventoryItemService.addInventoryItem(inventoryItemDto);
        StepVerifier.create(result)
                .expectError(UniqueKeyException.class)
                .verify();
    }

    @Test
    void updateInventoryItemNotFound() {
        InventoryItemDto inventoryItemDto = InventoryItemDto.builder().inventoryId(1).inventoryNumber(1).roomId(1).buildingId(1).build();
        BDDMockito.given(inventoryItemRepository.findById(1))
                .willReturn(Mono.empty());
        Mono<InventoryItem> result = inventoryItemService.updateInventoryItem(1, inventoryItemDto);
        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateInventoryItemSuccess() {
        InventoryItemDto inventoryItemDto = InventoryItemDto.builder().inventoryId(1).inventoryNumber(1).roomId(1).buildingId(1).build();
        InventoryItem existingInventoryItem = new InventoryItem(1, 1, 1);
        BDDMockito.given(inventoryItemRepository.findById(1))
                .willReturn(Mono.just(existingInventoryItem));
        BDDMockito.given(inventoryItemRepository.save(existingInventoryItem))
                .willReturn(Mono.just(existingInventoryItem));
        Mono<InventoryItem> result = inventoryItemService.updateInventoryItem(1, inventoryItemDto);
        StepVerifier.create(result)
                .expectNextMatches(inventoryItem ->
                        inventoryItem.getInventoryId().equals(1)
                                && inventoryItem.getInventoryNumber() == 1
                                && inventoryItem.getRoomId().equals(1)
                                && inventoryItem.getBuildingId().equals(1))
                .verifyComplete();
    }

    @Test
    void deleteInventoryItemSuccess() {
        BDDMockito.given(inventoryItemRepository.deleteById(1))
                .willReturn(Mono.empty());
        Mono<Boolean> result = inventoryItemService.deleteInventoryItem(1);
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getInventoryItemRoomInfoBySignNumberWrongSignNumberPattern() {
        Flux<InventoryItemWithRoomInfoDto> result = inventoryItemService.getInventoryItemRoomInfoBySignNumber("22A", 0, 10);
        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }


    @Test
    void getInventoryItemRoomInfoBySignNumberWrongInputPattern() {
        String inventorySignNumber = "23A";
        int page = 0, size = 10;

        Flux<InventoryItemWithRoomInfoDto> result = inventoryItemService.getInventoryItemRoomInfoBySignNumber(inventorySignNumber, page, size);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void getInventoryItemRoomInfoBySignNumberInventoryItemNotFound() {
        String inventorySignNumber = "A23";
        int page = 0, size = 10;

        BDDMockito.given(inventoryItemRepository.findByInventorySignAndNumber(inventorySignNumber.substring(0, 1), Integer.parseInt(inventorySignNumber.substring(1)), page, size))
                .willReturn(Flux.empty());

        Flux<InventoryItemWithRoomInfoDto> result = inventoryItemService.getInventoryItemRoomInfoBySignNumber(inventorySignNumber, page, size);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }


}



