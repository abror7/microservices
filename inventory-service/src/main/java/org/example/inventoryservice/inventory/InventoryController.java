package org.example.inventoryservice.inventory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventoryservice.inventory.dto.InventoriesByRoomResponseDto;
import org.example.inventoryservice.inventory.dto.InventoryDto;
import org.example.inventoryservice.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public Flux<Inventory> getAllInventories(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return inventoryService.getAllInventories(page - 1, size);

    }


    @GetMapping("/by-room-id/{roomId}")
    public Flux<InventoriesByRoomResponseDto> getInventoriesByRoomId(@PathVariable Integer roomId,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("Getting data... roomId: {}", roomId);
        return inventoryService.getInventoriesByRoomId(roomId, page - 1, size);

    }

    @GetMapping("/{id}")
    public Mono<Inventory> getInventoryById(@PathVariable Integer id) {
        return inventoryService.getInventoryById(id);
    }


    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> addInventory(@RequestBody InventoryDto inventoryDto) {
        log.info("new inventory adding {}", inventoryDto);
        return inventoryService.addInventory(inventoryDto)
                .map(savedInventory ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully saved!!",
                                        savedInventory),
                                HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> updateInventory(@PathVariable Integer id, @RequestBody InventoryDto inventoryDto) {
        log.info("updating inventory with id: {}, data: {}", id, inventoryDto);
        return inventoryService.updateInventory(id, inventoryDto)
                .map(updatedInventory ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully updated!!",
                                        updatedInventory),
                                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> deleteInventory(@PathVariable Integer id) {
        log.info("deleting inventory with id: {}", id);
        return inventoryService.deleteInventory(id)
                .map(deletedInventory ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully deleted!!",
                                        deletedInventory),
                                HttpStatus.OK));
    }


}
