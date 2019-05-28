package com.example.service_searcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.service_searcher.entity.PairRepository;
import com.example.service_searcher.entity.ReverseIndexPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class DocumentSearcher {

    @Autowired
    private PairRepository pairRepository;

    public List<Integer> getDocsBySentence(String sentence) {

//        List<String> words = Arrays.asList(sentence.split(" "));
//        List<ReverseIndexPair> pairs = new ArrayList<>();
//        for (String w: words){
//            pairs.add(
//                    pairRepository.findByWord(w)
//            );
//        }
//        List<List<Integer>> documentIds = new ArrayList<>();
//        for (ReverseIndexPair pair: pairs) {
//            documentIds.add(pair.getDocumentIds());
//        }

        Stream<String> words = Stream.of(sentence
                .replaceAll("[^a-zA-Zа-яА-Я ]", "")
                .toLowerCase().split(" "));

        Optional<List<Integer>> documentIds = words
                .map(p -> pairRepository.findByWord(p))
                .map(ReverseIndexPair::getDocumentIds)
                .reduce(this::intersectSortedList);

        return documentIds.orElse(Collections.emptyList());
    }

    private <T> List<T> intersect(List<T> l1, List<T> l2) {

        l2.retainAll(l1);
        return l2;

    }

    private List<Integer> intersectSortedList(List<Integer> l1, List<Integer> l2) {

        int i1 = 0;
        int i2 = 0;

        List<Integer> resultList = new ArrayList<>();

        while (i1 < l1.size() && i2 < l2.size()) {
            int obj1 = l1.get(i1);
            int obj2 = l2.get(i2);
            if (obj1 == obj2) {
                resultList.add(obj1);
            } else if (obj1 > obj2) {
                i2++;
            } else {
                i1++;
            }
        }

        return resultList;
    }

}
