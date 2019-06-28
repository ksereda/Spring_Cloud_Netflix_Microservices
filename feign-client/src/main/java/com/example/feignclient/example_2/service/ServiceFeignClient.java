package com.example.feignclient.example_2.service;

import com.example.feignclient.example_2.entity.EmployeeEntity;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import org.springframework.cloud.openfeign.FeignClient;
import java.util.List;

@FeignClient(name = "service_statistics", url = "http://localhost:8079/", fallback = Fallback.class)
public interface ServiceFeignClient {

    class FeignHolder {
        public static ServiceFeignClient create() {
            return HystrixFeign.builder().encoder(new GsonEncoder()).decoder(new GsonDecoder()).target(ServiceFeignClient.class, "http://localhost:8079/", new FallbackFactory<ServiceFeignClient>() {
                @Override
                public ServiceFeignClient create(Throwable throwable) {
                    return new ServiceFeignClient() {
                        @Override
                        public List<EmployeeEntity> getAllEmployeesList() {
                            System.out.println(throwable.getMessage());
                            return null;
                        }
                    };
                }
            });
        }
    }

    @RequestLine("GET /show")
    List<EmployeeEntity> getAllEmployeesList();

}
