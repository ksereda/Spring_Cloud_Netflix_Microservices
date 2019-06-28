package com.example.service_statistics.service;

import com.example.service_statistics.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StatisticsService extends MongoRepository<UserModel, String> {

    List<UserModel> getStatisticsById(String id);

}
