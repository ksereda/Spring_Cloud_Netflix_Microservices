//package com.example.feignclient;
//
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@FeignClient(name = "ConfigClient")
//@Component
//public interface ConfigClient {
//
//    @RequestMapping(method = RequestMethod.GET, value = "/hello")
//    String getHello();
//
//    @RequestMapping(method = RequestMethod.GET, value="/service-instances/{applicationName}")
//    List<ServiceInstance> getInstanceInfo(@PathVariable("applicationName") String applicationName);
//
//
//
//}
