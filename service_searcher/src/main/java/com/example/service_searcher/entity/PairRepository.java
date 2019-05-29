package com.example.service_searcher.entity;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PairRepository extends ReactiveMongoRepository<ReverseIndexPair, String> {

    Mono<ReverseIndexPair> findByWord(final String word);

}
