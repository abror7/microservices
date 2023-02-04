package org.example.inventoryservice.inventory;

import org.example.inventoryservice.exception.CustomBadRequestException;
import org.example.inventoryservice.exception.ResourceNotFoundException;
import org.example.inventoryservice.exception.UniqueKeyException;
import org.example.inventoryservice.inventory.dto.InventoryDto;
import org.example.inventoryservice.inventoryitem.InventoryItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private InventoryService inventoryService;


    @Test
    void getAllInventoriesShouldReturnAllInventories() {
        Flux<Inventory> allInventories = Flux.just(
                Inventory.builder().id(1).name("inventory1").description("description1").inventorySign("sign1").build(),
                Inventory.builder().id(2).name("inventory2").description("description2").inventorySign("sign2").build());
        when(inventoryRepository.findAll()).thenReturn(allInventories);

        Flux<Inventory> result = inventoryService.getAllInventories(0, 2);

        StepVerifier.create(result)
                .expectNextMatches(inventory -> inventory.getId().equals(1) && inventory.getName().equals("inventory1"))
                .expectNextMatches(inventory -> inventory.getId().equals(2) && inventory.getName().equals("inventory2"))
                .verifyComplete();
    }


    @Test
    void getAllInventoriesAndShouldReturnErrorWhenPageOrSizeIsLessThan1() {
        Flux<Inventory> result = inventoryService.getAllInventories(-1, 0);

        StepVerifier.create(result)
                .expectError(CustomBadRequestException.class)
                .verify();
    }

    @Test
    void getInventoryByIdAndShouldReturnInventory() {
        Mono<Inventory> inventoryMono = Mono.just(Inventory.builder().id(1).name("inventory1").description("description1").inventorySign("sign1").build());
        when(inventoryRepository.findById(1)).thenReturn(inventoryMono);

        Mono<Inventory> result = inventoryService.getInventoryById(1);

        StepVerifier.create(result)
                .expectNextMatches(inventory -> inventory.getId().equals(1) && inventory.getName().equals("inventory1"))
                .verifyComplete();
    }

    @Test
    void getInventoryByIdAndShouldReturnErrorWhenInventoryNotFound() {
        when(inventoryRepository.findById(1)).thenReturn(Mono.empty());

        Mono<Inventory> result = inventoryService.getInventoryById(1);

        StepVerifier.create(result)
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void addInventorySuccess() {
        InventoryDto inventoryDto = InventoryDto.builder()
                .name("Test Inventory")
                .description("Test Description")
                .inventorySign("Test Sign")
                .build();
        Inventory inventory = Inventory.builder()
                .name("Test Inventory")
                .description("Test Description")
                .inventorySign("Test Sign")
                .build();

        when(inventoryRepository.save(inventory)).thenReturn(Mono.just(inventory));

        Mono<Inventory> result = inventoryService.addInventory(inventoryDto);

        StepVerifier.create(result)
                .expectNext(inventory)
                .verifyComplete();
    }

    @Test
    void addInventoryFailure() {
        InventoryDto inventoryDto = InventoryDto.builder()
                .name("Test Inventory")
                .description("Test Description")
                .inventorySign("Test Sign")
                .build();
        Inventory newInventory = Inventory.builder()
                .name("Test Inventory")
                .description("Test Description")
                .inventorySign("Test Sign")
                .build();
        when(inventoryRepository.save(newInventory)).thenReturn(Mono.error(RuntimeException::new));

        Mono<Inventory> result = inventoryService.addInventory(inventoryDto);

        StepVerifier.create(result)
                .expectError(UniqueKeyException.class)
                .verify();
        verify(inventoryRepository, times(1)).save(newInventory);
    }

    @Test
    void updateInventoryTest() {
        InventoryDto inventoryDto = InventoryDto.builder()
                .id(1)
                .name("inventory1")
                .description("description1")
                .inventorySign("sign1")
                .build();

        Inventory updatedInventory = Inventory.builder()
                .id(inventoryDto.getId())
                .name(inventoryDto.getName())
                .description(inventoryDto.getDescription())
                .inventorySign(inventoryDto.getInventorySign())
                .build();

        when(inventoryRepository.findById(inventoryDto.getId()))
                .thenReturn(Mono.just(updatedInventory));

        when(inventoryRepository.save(updatedInventory))
                .thenReturn(Mono.just(updatedInventory));

        Mono<InventoryDto> inventory = inventoryService.updateInventory(1, inventoryDto);
        StepVerifier.create(inventory)
                .expectNext(inventoryDto)
                .verifyComplete();

        verify(inventoryRepository, times(1)).findById(inventoryDto.getId());
        verify(inventoryRepository, times(1)).save(updatedInventory);
    }

    @Test
    void deleteInventoryTest() {
        Integer id = 1;

        when(inventoryRepository.deleteById(id))
                .thenReturn(Mono.empty());

        Mono<Boolean> result = inventoryService.deleteInventory(id);
        StepVerifier.create(result)
                .verifyComplete();

        verify(inventoryRepository, times(1)).deleteById(id);
    }


}