package org.example.buildingservice.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.buildingservice.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public Flux<Room> getAllRooms(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {

        return roomService.getAllRooms(page - 1, size);

    }

    @GetMapping("/{id}")
    public Mono<RoomDto> getRoomById(@PathVariable Integer id) {
        return roomService.getRoomById(id);
    }


    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> addRoom(@RequestBody RoomDto roomDto) {
        log.info("new room adding {}", roomDto);
        return roomService.addRoom(roomDto)
                .map(savedRoom ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully saved!!",
                                        savedRoom),
                                HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> updateRoom(@PathVariable Integer id, @RequestBody RoomDto roomDto) {
        log.info("updating room with id: {}, data: {}", id, roomDto);
        return roomService.updateRoom(id, roomDto)
                .map(updatedRoom ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully updated!!",
                                        updatedRoom),
                                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> deleteRoom(@PathVariable Integer id) {
        log.info("deleting room with id: {}", id);
        return roomService.deleteRoom(id)
                .map(deletedRoom ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully deleted!!",
                                        deletedRoom),
                                HttpStatus.OK));
    }


}
