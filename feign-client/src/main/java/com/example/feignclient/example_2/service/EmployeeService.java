package com.example.feignclient.example_2.service;

import com.example.feignclient.example_2.entity.EmployeeEntity;
import com.example.feignclient.example_2.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository repository;

    @Transactional
    public List<EmployeeEntity> getAllEmployees() {
        List<EmployeeEntity> result = (List<EmployeeEntity>) repository.findAll();

        if (result.size() > 0) {
            return result;
        } else {
            return new ArrayList<>();
        }

    }

    public EmployeeEntity[] convertListToArray(List<EmployeeEntity> list) {
        return list.toArray(new EmployeeEntity[list.size()]);
    }

}
