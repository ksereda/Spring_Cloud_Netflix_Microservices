package com.example.store_original_data.dao;

import com.example.store_original_data.entity.Text;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TextRepository extends ReactiveMongoRepository<Text, Integer> {

    Mono<Text> findById(final Integer id);
    Mono insert(final Text text);

}
