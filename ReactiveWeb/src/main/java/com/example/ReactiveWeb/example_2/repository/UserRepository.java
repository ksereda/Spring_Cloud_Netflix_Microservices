package com.example.ReactiveWeb.example_2.repository;

import com.example.ReactiveWeb.example_2.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

}