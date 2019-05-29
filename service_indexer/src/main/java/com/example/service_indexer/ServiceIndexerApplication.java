package com.example.service_indexer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@EnableDiscoveryClient
@EnableAsync
@SpringBootApplication
public class ServiceIndexerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceIndexerApplication.class, args);
	}

}
