package com.example.service_searcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class SearchDocumentsController {

    @Autowired
    private DocumentSearcher documentSearcher;

    @GetMapping(value="/searchget/{query}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<List<Integer>> searchGetDocuments(@PathVariable String query) {
        Mono<List<Integer>> result = documentSearcher.getDocsBySentence(query);
        return result;
    }

    @PostMapping(value = "/search", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<List<Integer>> searchDocuments(@RequestBody String query) {
        Mono<List<Integer>> result = documentSearcher.getDocsBySentence(query);
        return result;
    }

    @PostMapping(value = "/searchmany", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Integer>> searchManyDocuments(@RequestBody List<String> query) {
        return documentSearcher.getManyDocs(query);
    }

}
