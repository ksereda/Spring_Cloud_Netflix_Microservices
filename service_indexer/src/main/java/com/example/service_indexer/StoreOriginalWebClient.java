package com.example.service_indexer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StoreOriginalWebClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<Integer> saveOriginalText(String text) {
        log.info("save to DB text: " + text);

        return webClientBuilder
                .build()
                .post()
                .uri("http://store_original_data/text")
//                .uri("http://localhost:8083/text")
//                .accept(MediaType.TEXT_EVENT_STREAM)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just(text), String.class)
                .retrieve()
                .bodyToMono(Integer.class);
    }

}