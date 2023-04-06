package org.example.buildingservice.building;

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
@RequestMapping("/api/v1/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public Flux<BuildingDto> getAllBuildings(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Get all buildings");
        return buildingService.getAllBuildings(page - 1, size);
//        try {
//            int numThreads = 5;
//
//            // Create and start each thread
//            for (int i = 0; i < numThreads; i++) {
//                Thread thread = new Thread(() -> {
//                    // Code to be executed by each thread goes here
//                    log.warn("Thread " + Thread.currentThread().getName() + " is running.");
//                });
//                thread.start();
//            }
//            log.warn("Sleep for 3 sec");
//
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        log.warn("Finished...");
//
//
//        return Flux.empty();
    }

    @GetMapping("/{id}")
    public Mono<BuildingDto> getBuildingById(@PathVariable Integer id) {
        return buildingService.getBuildingById(id);
    }

    @GetMapping("/check-if-exist/{id}")
    public Mono<Boolean> checkBuildingIfExist(@PathVariable Integer id) {
        return buildingService.checkBuildingIfExist(id);
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> addBuilding(@RequestBody BuildingDto buildingDto) {
        log.info("new building adding {}", buildingDto);
        return buildingService.addBuilding(buildingDto)
                .map(savedBuilding ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully saved!!",
                                        savedBuilding),
                                HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> updateBuilding(@PathVariable Integer id, @RequestBody BuildingDto buildingDto) {
        return buildingService.updateBuilding(id, buildingDto)
                .map(updatedBuilding ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully updated!!",
                                        updatedBuilding),
                                HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse>> deleteBuilding(@PathVariable Integer id) {
        return buildingService.deleteBuilding(id)
                .map(deletedBuilding ->
                        new ResponseEntity<>(
                                new ApiResponse(
                                        true,
                                        "Successfully deleted!!",
                                        deletedBuilding),
                                HttpStatus.OK));
    }
}
