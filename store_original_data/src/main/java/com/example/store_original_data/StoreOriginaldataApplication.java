package com.example.store_original_data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableDiscoveryClient
@EnableMongoRepositories
@SpringBootApplication
public class StoreOriginaldataApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreOriginaldataApplication.class, args);
	}

}

