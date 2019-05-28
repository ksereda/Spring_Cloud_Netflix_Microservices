package com.example.store_original_data.dao;

import com.example.store_original_data.entity.Text;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TextRepository extends MongoRepository<Text, Integer> {

    Optional<Text> findById(final Integer id);
    List<Text> findAll();
    Text insert(final Text text);

}
