package com.example.hystrix.example_1;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Date;

@Service
public class UserService {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "callUserService_Fallback")
    public String callUserService(String group) {
        String response = restTemplate
                .exchange("http://localhost:8077/getUsersDetailsByGroup/{group}"
                        , HttpMethod.GET
                        , null
                        , new ParameterizedTypeReference<String>() {
                        }, group).getBody();

        return "It's OK: group: " + group + " users details " + response + new Date();
    }

    @SuppressWarnings("unused")
    private String callUserService_Fallback(String group) {
        return "Error! " + new Date();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
