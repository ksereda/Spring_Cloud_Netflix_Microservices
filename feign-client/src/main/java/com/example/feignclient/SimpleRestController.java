package com.example.feignclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class SimpleRestController {

    @Autowired
    private ConfigClient configClient;

    @RequestMapping("/helloworld")
    public String getHello(){
        return this.configClient.getHello();
    }

    @RequestMapping("/service/{applicationName}")
    public List<ServiceInstance> serviceInstancesList(
            @PathVariable String applicationName){

        return this.configClient.getInstanceInfo(applicationName);
    }

}
