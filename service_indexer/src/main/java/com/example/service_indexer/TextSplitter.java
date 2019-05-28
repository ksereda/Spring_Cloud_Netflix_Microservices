package com.example.service_indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TextSplitter {

    @Autowired
    private StoreOriginalFeign storeOriginal;

    @Autowired
    private InverseIndexSaver inverseIndexSaver;

    @Value("${words.blacklist}")
    public String[] blackListRaw;

    private Set<String> blackList;

    @PostConstruct
    public void init() {
        blackList = new HashSet<>(Arrays.asList(blackListRaw));
    }


    public Integer process(String body) {
        StoreOriginalFeign.SaverResponse storeResponse = storeOriginal.saveOriginalText(body);
        Integer documentId = storeResponse.getId();
        System.out.println("DocumentId: " + documentId);
        List<Pair> invertedIndex = invertText(body, documentId);
        System.out.println("Found " + invertedIndex.size() + " words");
        inverseIndexSaver.storeReverseIndex(invertedIndex);
        return documentId;
    }

    private List<Pair> invertText(String body, Integer documentId) {
        String[] words = body
                .replaceAll("[^a-zA-Zа-яА-Я ]", "")
                .split(" ");

        List<String> list = Stream.of(words)
                .map(String::toLowerCase)
                .filter(p -> ! blackList.contains(p))
                .collect(Collectors.toList());

        return new HashSet<>(list)
                .stream()
                .map(p -> new Pair(p, documentId))
                .collect(Collectors.toList());


    }
}