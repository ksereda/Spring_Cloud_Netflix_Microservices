package com.example.hystrix.exqmple_3;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 *  Чтобы разрешить Feign использовать класс Fallback, который может обрабатывать сообщения об ошибках, нам необходимо указать  в аннотации
 *  fallbackFactory
 *
 */

@FeignClient(name = "service_statistics", fallbackFactory = StatisticFallbackFactory.class)
public interface ServiceFeignClient {

    @GetMapping("/users/{id}/statistics")
    public List<UserModel> getStatistics(@PathVariable String id);

}
