package org.thetasinner.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class Service {
    @GetMapping("/hello/{name}")
    public Mono<ResponseEntity<Data>> hello(@PathVariable(value = "name") String name) {
        return Mono.just(ResponseEntity.ok(new Data(name)));
    }
}
