package com.example.feignclient.example_2.controller;

import java.util.List;
import java.util.Optional;
import com.example.feignclient.example_2.entity.EmployeeEntity;
import com.example.feignclient.example_2.exception.RecordNotFoundException;
import com.example.feignclient.example_2.service.EmployeeService;
import com.example.feignclient.example_2.service.ServiceFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class EmployeeMvcController {

    @Autowired
    private EmployeeService service;
    private ServiceFeignClient serviceFeignClient;

    @RequestMapping
    public String showEmployeeById(Model model, @PathVariable("id") Optional<Long> id) throws RecordNotFoundException {
        model.addAttribute("employee", new EmployeeEntity());
        return "choose-employee";
    }

    @RequestMapping(path = "/getAllDataFromAddService")
    public String getData2(Model model) {
        List<EmployeeEntity> list = ServiceFeignClient.FeignHolder.create().getAllEmployeesList();
        model.addAttribute("employees", list);
        return "resultlist-employees";
    }

}
