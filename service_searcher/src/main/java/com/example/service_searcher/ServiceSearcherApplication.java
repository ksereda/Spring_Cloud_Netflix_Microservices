package com.example.service_searcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceSearcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceSearcherApplication.class, args);
	}

}
