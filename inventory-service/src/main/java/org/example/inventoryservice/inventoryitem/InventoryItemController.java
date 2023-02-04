package org.example.inventoryservice.inventoryitem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemDto;
import org.example.inventoryservice.inventoryitem.dto.InventoryItemWithRoomInfoDto;
import org.example.inventoryservice.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory-items")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @GetMapping("/search-by-sign-number/{inventorySignNumber}")
    public Flux<InventoryItemWithRoomInfoDto> getInventoryItemRoomInfoBySignNumber(@PathVariable String inventorySignNumber,
                                                                                   @RequestParam(defaultValue = "1") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        return inventoryItemService.getInventoryItemRoomInfoBySignNumber(inventorySignNumber, page - 1, size);
    }

    @GetMapping
    public Flux<InventoryItem> getAllInventoryItems(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {

        return inventoryItemService.getAllInventoryItems(page - 1, size);

    }


    @GetMapping("/{id}")
    public Mono<InventoryItemDto> getInventoryItemById(@PathVariable Integer id) {
        return inventoryItemService.getInventoryItemById(id);
    }


    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> addInventoryItem(@RequestBody InventoryItemDto inventoryItemDto) {
        log.info("new inventoryItem adding {}", inventoryItemDto);
        // TODO: 30/01/23 add validation
        return inventoryItemService.addInventoryItem(inventoryItemDto)
                .map(savedInventoryItem ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully saved!!",
                                        savedInventoryItem),
                                HttpStatus.CREATED));
    }



    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> updateInventoryItem(@PathVariable Integer id, @RequestBody InventoryItemDto inventoryItemDto) {
        log.info("updating inventoryItem with id: {}, data: {}", id, inventoryItemDto);
        return inventoryItemService.updateInventoryItem(id, inventoryItemDto)
                .map(updatedInventoryItem ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully updated!!",
                                        updatedInventoryItem),
                                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> deleteInventoryItem(@PathVariable Integer id) {
        log.info("deleting inventoryItem with id: {}", id);
        return inventoryItemService.deleteInventoryItem(id)
                .map(deletedInventoryItem ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully deleted!!",
                                        deletedInventoryItem),
                                HttpStatus.OK));
    }


}
