package com.example.ftp_bucket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FtpBucketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpBucketServiceApplication.class, args);
	}

}
