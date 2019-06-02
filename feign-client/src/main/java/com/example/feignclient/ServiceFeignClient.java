package com.example.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service_statistics")
public interface ServiceFeignClient {

    @GetMapping("/users/{id}/statistics")
    public List<UserModel> getStatistics(@PathVariable String id);

}
