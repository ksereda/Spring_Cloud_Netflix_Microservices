package com.example.service_statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users/{id}/statistics")
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    public List<UserModel> getStatistics(@PathVariable String id) {
        List<UserModel> statisticsList = statisticsService.getStatisticsById(id);
        return statisticsList;
    }

}
