package org.example.buildingservice.building;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class MyController {


    private final MyService myService;

    @GetMapping("/process")
    public Mono<String> process() {
        log.warn("This code runs on Thread A");

        log.info("MDC {}", MDC.getCopyOfContextMap());
        return Mono.just("Processing")
                .flatMap(value -> myService.processData(value))
                .map(result -> {
                    log.warn("This code runs on Thread C");
                    return "Processed: " + result;
                });
    }
}