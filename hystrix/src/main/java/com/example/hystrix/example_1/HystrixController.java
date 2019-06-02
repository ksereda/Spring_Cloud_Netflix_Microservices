package com.example.hystrix.example_1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HystrixController {

    @Autowired
    UserService userservice;

    @RequestMapping(value = "/getGroupDetails/{group}", method = RequestMethod.GET)
    public String getUsers(@PathVariable String group) {
        return userservice.callUserService(group);
    }

}
