package com.example.hystrix.example_2;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 *  Здесь мы предоставляем запасной класс в аннотации @FeignClient
 */

@FeignClient(name = "service_statistics", fallback = UsersFallback.class)
public interface ServiceFeignClient {

    @GetMapping("/users/{id}/statistics")
    public List<UserModel> getStatistics(@PathVariable String id);

}
