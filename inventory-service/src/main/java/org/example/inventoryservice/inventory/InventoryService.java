package org.example.inventoryservice.inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventoryservice.exception.*;
import org.example.inventoryservice.inventory.dto.InventoriesByRoomResponseDto;
import org.example.inventoryservice.inventory.dto.InventoryDto;
import org.example.inventoryservice.inventoryitem.InventoryItem;
import org.example.inventoryservice.inventoryitem.InventoryItemRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public Flux<Inventory> getAllInventories(int page, int size) {
        if (invalidPageOrSize(page, size)) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return inventoryRepository.findAll()
                .skip((long) page * size)
                .take(size);
    }

    public Flux<InventoriesByRoomResponseDto> getInventoriesByRoomId(Integer roomId, int page, int size) {
        if (invalidPageOrSize(page, size)) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return inventoryRepository.getInventoriesByRoomId(roomId, page, size);
    }

    private static boolean invalidPageOrSize(int page, int size) {
        return page < 0 || size <= 0;
    }

    public Mono<Inventory> getInventoryById(Integer id) {
        return inventoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Inventory", id)));
    }

    @Transactional
    public Mono<Inventory> addInventory(InventoryDto inventoryDto) {
        Inventory newInventory = Inventory.builder()
                .name(inventoryDto.getName())
                .description(inventoryDto.getDescription())
                .inventorySign(inventoryDto.getInventorySign())
                .build();
        return inventoryRepository.save(newInventory)
                .onErrorMap(e -> new UniqueKeyException("Inventory with the same name and sign already exists."))
                .flatMap(savedInventory ->
                        {
                            if (shouldSaveInventoryItems(inventoryDto))
                                return saveInventoryItems(inventoryDto, savedInventory);
                            return Mono.just(savedInventory);
                        }
                );
    }

    public Mono<InventoryDto> updateInventory(Integer id, InventoryDto inventoryDto) {
        return inventoryRepository.findById(id)
                .flatMap(existingInventory -> {
                    existingInventory.setName(inventoryDto.getName());
                    existingInventory.setDescription(inventoryDto.getDescription());
                    existingInventory.setInventorySign(inventoryDto.getInventorySign());
                    return inventoryRepository.save(existingInventory);
                })
                .map(this::convertToInventoryDto)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Inventory", id)));
    }

    public Mono<Boolean> deleteInventory(Integer id) {
        return inventoryRepository.deleteById(id)
                .onErrorMap(e -> new CustomBadRequestException("You can't delete an inventory with id: " + id))
                .flatMap(unused -> Mono.just(true));
    }

    private InventoryDto convertToInventoryDto(Inventory inventory) {
        return InventoryDto.builder()
                .id(inventory.getId())
                .name(inventory.getName())
                .description(inventory.getDescription())
                .inventorySign(inventory.getInventorySign())
                .build();
    }

    private boolean shouldSaveInventoryItems(InventoryDto dto) {
        return (dto.getInventoryItems() != null && dto.getInventoryItems().size() != 0) ||
                dto.getQuantity() != null ||
                (dto.getStartNumberOfInventoryItem() != null && dto.getEndNumberOfInventoryItem() != null);
    }

    private Mono<Inventory> saveInventoryItems(InventoryDto inventoryDto, Inventory savedInventory) {
        List<InventoryItem> inventoryItems = new ArrayList<>();
        // TODO: 01/02/23 check if building exist
//        check if user is adding specific inventory items with number and building id
        if (inventoryDto.getInventoryItems() != null && inventoryDto.getInventoryItems().size() != 0) {
            inventoryItems = inventoryDto.getInventoryItems().stream()
                    .map(inventoryItemDto -> {
//                                  Check if required information is missing
                                if (inventoryItemDto.getInventoryNumber() == null || inventoryItemDto.getBuildingId() == null)
                                    throw new CustomBadRequestException("Required information is missing");
                                return InventoryItem.builder()
                                        .inventoryId(savedInventory.getId())
                                        .inventoryNumber(inventoryItemDto.getInventoryNumber())
                                        .buildingId(inventoryItemDto.getBuildingId())
                                        .build();
                            }
                    ).collect(Collectors.toList());
        } else {
            // check if user input both quantity number and (starting or ending number)
            if (inventoryDto.getQuantity() != null &&
                    (inventoryDto.getStartNumberOfInventoryItem() != null ||
                            inventoryDto.getEndNumberOfInventoryItem() != null)) {
                return Mono.error(() -> new CustomBadRequestException("You should input either quantity or start and end numbers"));
            }
//          Determine the starting number of inventory items
            int start = inventoryDto.getStartNumberOfInventoryItem() != null ?
                    inventoryDto.getStartNumberOfInventoryItem() : 1;
//          Determine the ending number of inventory items
            Integer end = inventoryDto.getEndNumberOfInventoryItem() != null ?
                    inventoryDto.getEndNumberOfInventoryItem() : inventoryDto.getQuantity();

            Integer buildingId = inventoryDto.getBuildingId();
//          Check if required information is missing
            if (end == null || buildingId == null)
                return Mono.error(() -> new CustomBadRequestException("Required information is missing"));
//          Check if the start number is greater than the end number
            if (start > end)
                return Mono.error(WrongInputRangeException::new);
//          Create a list of InventoryItem objects
            for (int i = start; i <= end; i++) {
                inventoryItems.add(
                        InventoryItem.builder()
                                .inventoryId(savedInventory.getId())
                                .inventoryNumber(i)
                                .buildingId(buildingId)
                                .build()
                );
            }
        }
        return inventoryItemRepository.saveAll(inventoryItems)
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException) {
                        String errorMsg = "InventoryItem with the same sign and number already exists.";
                        log.error(errorMsg);
                        throw new UniqueKeyException(errorMsg);
                    } else if (e instanceof CustomBadRequestException) {
                        log.error(e.getMessage());
                        throw new CustomBadRequestException(e.getMessage());
                    } else {
                        log.error(e.getMessage());
                        throw new CustomBadRequestException(); // TODO: 31/01/23 change with genereal custom exception
                    }
                })
                .then(Mono.just(savedInventory));
    }

}
