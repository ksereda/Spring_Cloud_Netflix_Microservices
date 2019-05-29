package com.example.service_indexer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class TextSplitter {

//    @Autowired
//    private StoreOriginalFeign storeOriginal;

    @Autowired
    private InverseIndexSaver inverseIndexSaver;

    @Autowired
    private StoreOriginalWebClient storeOriginalWebClient;

    @Value("${wordsblacklist}")
    public String[] blackListRaw;

    private Set<String> blackList;

    @PostConstruct
    public void init() {
        blackList = new HashSet<>(Arrays.asList(blackListRaw));
    }


    public Mono<Integer> process(String body) {

        Mono<Integer> documentId = storeOriginalWebClient.saveOriginalText(body).flatMap(s -> {
            List<Pair> invertedIndex = invertText(body, s);
            inverseIndexSaver.storeReverseIndex(invertedIndex);
            return Mono.just(s);
        });
        return documentId;
    }

    private List<Pair> invertText(String body, Integer documentId) {
        String[] words = body
                .replaceAll("[^a-zA-Zа-яА-Я ]", "")
                .split(" ");

        List<String> list = Stream.of(words)
                .map(String::toLowerCase)
                .filter(p -> !blackList.contains(p))
                .collect(Collectors.toList());

        return new HashSet<>(list)
                .stream()
                .map(p -> new Pair(p, documentId))
                .collect(Collectors.toList());


    }
}