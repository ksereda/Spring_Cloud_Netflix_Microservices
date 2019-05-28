package com.example.service_indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InverseIndexSaver {

    @Autowired
    private StoreReverseIndexFeign storeReverseIndex;

    @Async
    public void storeReverseIndex(List<Pair> invertedIndex) {
        storeReverseIndex.storeReverseIndex(invertedIndex);
        System.out.println("Index saved");
    }


}
