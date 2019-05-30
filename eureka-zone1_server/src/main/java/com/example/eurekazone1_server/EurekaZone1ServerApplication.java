package com.example.eurekazone1_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaZone1ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaZone1ServerApplication.class, args);
	}

}
