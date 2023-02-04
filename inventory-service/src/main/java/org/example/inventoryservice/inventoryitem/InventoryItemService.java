package org.example.inventoryservice.inventoryitem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventoryservice.exception.CustomBadRequestException;
import org.example.inventoryservice.exception.ResourceNotFoundException;
import org.example.inventoryservice.exception.UniqueKeyException;
import org.example.inventoryservice.inventory.InventoryRepository;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemDto;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemWithRoomInfoDto;
import org.example.inventoryservice.inventoryitem.dto.RoomDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryRepository inventoryRepository;
    private final WebClient.Builder webClientBuilder;


    public Flux<InventoryItem> getAllInventoryItems(int page, int size) {
        if (page < 0 || size <= 0) {
            log.error("Bad request...");
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        }
        return inventoryItemRepository.findAll()
                .skip((long) page * size)
                .take(size);
    }

    public Mono<InventoryItemDto> getInventoryItemById(Integer id) {
        return inventoryItemRepository.getInventoryItemById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("InventoryItem", id)));
    }

    public Mono<InventoryItem> addInventoryItem(InventoryItemDto inventoryItemDto) {
        InventoryItem build = new InventoryItem(
                inventoryItemDto.getInventoryId(),
                inventoryItemDto.getInventoryNumber(),
                inventoryItemDto.getBuildingId()
        );
        return inventoryItemRepository.save(build)
                .onErrorMap(e -> new UniqueKeyException("InventoryItem with the same name already exists."));
    }

    public Mono<InventoryItem> updateInventoryItem(Integer id, InventoryItemDto inventoryItemDto) {
        // TODO: 01/02/23 check if room still exist
        return inventoryItemRepository.findById(id)
                .flatMap(existingInventoryItem -> {
                    existingInventoryItem.setInventoryNumber(inventoryItemDto.getInventoryNumber());
                    existingInventoryItem.setRoomId(inventoryItemDto.getRoomId());
                    existingInventoryItem.setBuildingId(inventoryItemDto.getBuildingId());
                    existingInventoryItem.setInventoryId(inventoryItemDto.getInventoryId());
                    return inventoryItemRepository.save(existingInventoryItem);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("InventoryItem", id)));
    }

    public Mono<Boolean> deleteInventoryItem(Integer id) {
        return inventoryItemRepository.deleteById(id)
                .flatMap(unused -> Mono.just(true));
    }

    public Flux<InventoryItemWithRoomInfoDto> getInventoryItemRoomInfoBySignNumber(String inventorySignNumber, int page, int size) {
        if (page < 0 || size <= 0)
            return Flux.error(() -> new CustomBadRequestException("Page and size should not be less than 1"));
        String pattern = "(\\p{Alpha}+)(\\d+)";
        Matcher matcher = Pattern.compile(pattern).matcher(inventorySignNumber);
        if (!matcher.find())
            return Flux.error(new CustomBadRequestException("Wrong input patter. You should input letters first. Example: \"A23\""));
        String inventorySign = matcher.group(1);
        Integer inventoryNumber = Integer.valueOf(matcher.group(2));

        return inventoryItemRepository.findByInventorySignAndNumber(inventorySign, inventoryNumber, page, size)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Inventory item with room not found")))
                .flatMap(inventoryItemDto -> webClientBuilder.build()
                        .get()
                        .uri("http://BUILDING-SERVICE/api/v1/rooms/{id}", inventoryItemDto.getRoomId())
                        .retrieve()
                        .bodyToMono(RoomDto.class)
                        .onErrorMap(throwable -> {
                            System.out.println(throwable.getMessage());
                            return new ResourceNotFoundException("Room", inventoryItemDto.getRoomId());

                        })
                        .map(roomDto -> new InventoryItemWithRoomInfoDto(inventoryItemDto, roomDto))); // todo: Refactor

    }
}
