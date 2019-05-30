package com.example.eurekazone2_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaZone2ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaZone2ServerApplication.class, args);
	}

}
