package org.example.buildingservice.building;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class MyService {

    public Mono<String> processData(String data) {
        log.warn("This code runs on Thread B");
        return Mono.fromCallable(() -> {
            Thread.sleep(1000);
            return data + " in background";
        }).subscribeOn(Schedulers.boundedElastic());
    }
}