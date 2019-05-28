package com.example.service_searcher.entity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PairRepository extends MongoRepository<ReverseIndexPair, String> {

    ReverseIndexPair findByWord(final String word);

}
