package com.example.store_inverted_index.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReverseIndexPair {

    private String word;
    private List<Integer> documentIds;

    public ReverseIndexPair(String word, Integer docId) {
        this.word = word;
        this.documentIds = Collections.singletonList(docId);
    }

    public ReverseIndexPair(SimplePair simplePair) {
        this(simplePair.getWord(), simplePair.getDocumentId());
    }

}
