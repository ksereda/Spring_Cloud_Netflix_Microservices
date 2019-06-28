package com.example.feignclient.example_2.service;

import com.example.feignclient.example_2.entity.EmployeeEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class Fallback implements ServiceFeignClient {

    @Override
    public List<EmployeeEntity> getAllEmployeesList() {
        return new ArrayList<>();
    }

}
