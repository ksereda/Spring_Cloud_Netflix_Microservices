package com.example.hystrix.example_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableFeignClients
public class HystrixApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixApplication.class, args);
	}

}
