package com.example.feignclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class FeignClientApplication {

//    @Autowired
//    private ConfigClient configClient;
//
//    @PostConstruct
//    public void test() {
//        System.out.println(configClient.getHello());
//    }

	public static void main(String[] args) {
		SpringApplication.run(FeignClientApplication.class, args);
	}

}

