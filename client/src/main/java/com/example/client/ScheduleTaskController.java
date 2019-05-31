package com.example.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RefreshScope
@RestController
public class ScheduleTaskController {

    @Value("${myname}")
    private String name;

    @RequestMapping("/name")
    String getValue() {
        return this.name;
    }

    @Scheduled(fixedRate = 1000)
    public void showValue(){
        System.out.println(name);
    }

}
