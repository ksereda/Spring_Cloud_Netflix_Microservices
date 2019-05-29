package com.example.service_indexer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.net.URISyntaxException;


@Slf4j
@RestController
public class TextController {

    @Autowired
    private TextSplitter textSplitter;

    @PostMapping("/text")
    public Mono<Integer> processText(@RequestBody String body) throws URISyntaxException {
        log.info("got body: " + body);
        return textSplitter.process(body);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
